/* My orders page */
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

let members = [];

async function init() {
    members = await api("/api/members");
    const select = $("#member-select");
    select.innerHTML = members.map(m => `<option value="${m.id}">${m.fullName}</option>`).join("");
    select.addEventListener("change", loadOrders);
    await loadOrders();
}

async function loadOrders() {
    const container = $("#orders-list");
    const memberId = select_value();
    try {
        const orders = await api("/api/orders?memberId=" + memberId);
        if (!orders.length) {
            container.innerHTML = "<p>No orders yet. <a href='index.html'>Place your first order</a>.</p>";
            return;
        }
        container.innerHTML = orders.map(order => `
            <div class="card">
                <h2>Order #${order.id}
                    <span class="status-pill status-${order.status}">${order.status.replace(/_/g, " ")}</span>
                </h2>
                <p>Pickup week: ${order.pickupWeekLabel} · Placed: ${order.createdAt}</p>
                <table>
                    <thead><tr><th>Product</th><th>Qty</th><th>Unit price</th><th>Subtotal</th></tr></thead>
                    <tbody>
                    ${order.items.map(i => `
                        <tr><td>${i.productName}</td><td>${i.quantity}</td>
                        <td>$${money(i.unitPrice)}</td><td>$${money(i.subtotal)}</td></tr>`).join("")}
                    </tbody>
                </table>
                <p><strong>Total: $${money(order.totalAmount)}</strong></p>
                ${order.status === "PENDING" || order.status === "CONFIRMED"
                    ? `<button class="small warn" onclick="cancelOrder(${order.id})">Cancel order</button>` : ""}
            </div>`).join("");
    } catch (err) {
        container.textContent = err.message;
    }
}

function select_value() { return $("#member-select").value; }

async function cancelOrder(orderId) {
    try {
        await api(`/api/orders/${orderId}/cancel?memberId=${select_value()}`, { method: "PUT" });
        await loadOrders();
    } catch (err) {
        alert(err.message);
    }
}

document.addEventListener("DOMContentLoaded", init);
