/* Volunteer admin page: manage pickup weeks, products and orders */
"use strict";

function $(sel) { return document.querySelector(sel); }

async function api(url, options) {
    const res = await fetch(url, options);
    const body = await res.json().catch(() => ({}));
    if (!res.ok) {
        throw new Error(body.error || ("Request failed: " + res.status));
    }
    return body;
}

function money(n) { return Number(n).toFixed(2); }

async function init() {
    await Promise.all([loadWeeks(), loadProducts()]);
    await loadWeekSelect();
    await loadOrders();
}

/* ---------- pickup weeks ---------- */

async function loadWeeks() {
    const weeks = await api("/api/pickup-weeks");
    const tbody = $("#week-table tbody");
    tbody.innerHTML = weeks.map(w => `
        <tr>
            <td>${w.id}</td>
            <td>${w.weekStart} → ${w.weekEnd}</td>
            <td>${w.orderDeadline}</td>
            <td><span class="status-pill status-${w.status}">${w.status}</span></td>
            <td>${w.notes || ""}</td>
            <td>${w.status === "OPEN"
                ? `<button class="small" onclick="setWeekStatus(${w.id}, 'CLOSED')">Close</button>`
                : `<button class="small" onclick="setWeekStatus(${w.id}, 'OPEN')">Reopen</button>`}</td>
        </tr>`).join("");
}

async function setWeekStatus(id, status) {
    const week = await api("/api/pickup-weeks/" + id);
    week.status = status;
    await api("/api/pickup-weeks/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(week)
    });
    await Promise.all([loadWeeks(), loadWeekSelect()]);
}

async function createWeek(e) {
    e.preventDefault();
    const week = {
        weekStart: $("#week-start").value,
        weekEnd: $("#week-end").value,
        orderDeadline: $("#week-deadline").value,
        status: "OPEN",
        notes: $("#week-notes").value
    };
    try {
        await api("/api/pickup-weeks", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(week)
        });
        $("#week-form").reset();
        await Promise.all([loadWeeks(), loadWeekSelect()]);
    } catch (err) {
        alert(err.message);
    }
}

/* ---------- products ---------- */

async function loadProducts() {
    const products = await api("/api/products?all=true");
    const tbody = $("#product-table tbody");
    tbody.innerHTML = products.map(p => `
        <tr>
            <td>${p.id}</td>
            <td>${p.name}</td>
            <td>${p.category || ""}</td>
            <td>${p.unit || ""}</td>
            <td>$${money(p.price)}</td>
            <td>${p.stockQuantity}</td>
            <td>${p.active ? "yes" : "no"}</td>
            <td>${p.active
                ? `<button class="small warn" onclick="toggleProduct(${p.id}, false)">Deactivate</button>`
                : `<button class="small" onclick="toggleProduct(${p.id}, true)">Activate</button>`}</td>
        </tr>`).join("");
}

async function toggleProduct(id, active) {
    const p = await api("/api/products/" + id);
    p.active = active;
    await api("/api/products/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(p)
    });
    await loadProducts();
}

async function createProduct(e) {
    e.preventDefault();
    const product = {
        name: $("#product-name").value,
        category: $("#product-category").value,
        unit: $("#product-unit").value,
        price: Number($("#product-price").value),
        stockQuantity: Number($("#product-stock").value),
        active: true
    };
    try {
        await api("/api/products", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(product)
        });
        $("#product-form").reset();
        await loadProducts();
    } catch (err) {
        alert(err.message);
    }
}

/* ---------- orders ---------- */

async function loadWeekSelect() {
    const weeks = await api("/api/pickup-weeks");
    $("#week-select").innerHTML = weeks.map(w =>
        `<option value="${w.id}">#${w.id} ${w.weekStart} → ${w.weekEnd} (${w.status})</option>`).join("");
    $("#week-select").addEventListener("change", loadOrders);
}

async function loadOrders() {
    const weekId = $("#week-select").value;
    if (!weekId) return;
    try {
        const orders = await api("/api/orders?pickupWeekId=" + weekId);
        const tbody = $("#order-table tbody");
        tbody.innerHTML = orders.map(o => `
            <tr>
                <td>${o.id}</td>
                <td>${o.memberName}</td>
                <td>$${money(o.totalAmount)}</td>
                <td><span class="status-pill status-${o.status}">${o.status.replace(/_/g, " ")}</span></td>
                <td>${o.items.map(i => `${i.productName}×${i.quantity}`).join(", ")}</td>
                <td>
                    ${o.status === "PENDING" ? `<button class="small" onclick="setStatus(${o.id}, 'CONFIRMED')">Confirm</button>` : ""}
                    ${o.status === "CONFIRMED" ? `<button class="small" onclick="setStatus(${o.id}, 'READY_FOR_PICKUP')">Ready</button>` : ""}
                    ${o.status === "READY_FOR_PICKUP" ? `<button class="small" onclick="setStatus(${o.id}, 'COMPLETED')">Complete</button>` : ""}
                    ${o.status !== "CANCELLED" && o.status !== "COMPLETED" ? `<button class="small warn" onclick="setStatus(${o.id}, 'CANCELLED')">Cancel</button>` : ""}
                </td>
            </tr>`).join("");
        if (!orders.length) {
            tbody.innerHTML = "<tr><td colspan='6'>No orders for this week yet.</td></tr>";
        }
    } catch (err) {
        alert(err.message);
    }
}

async function setStatus(orderId, status) {
    try {
        await api(`/api/orders/${orderId}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status })
        });
        await loadOrders();
    } catch (err) {
        alert(err.message);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    init();
    $("#week-form").addEventListener("submit", createWeek);
    $("#product-form").addEventListener("submit", createProduct);
});
