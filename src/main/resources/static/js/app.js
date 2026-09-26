/* Ordering page: product catalog + basket + place order */
"use strict";

const state = {
    products: [],
    openWeek: null,
    members: [],
    cart: {} // productId -> quantity
};

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
    await Promise.all([loadMembers(), loadOpenWeek(), loadProducts()]);
    renderBanner();
    renderProducts();
    renderCart();
}

async function loadMembers() {
    state.members = await api("/api/members");
    const select = $("#member-select");
    select.innerHTML = state.members
        .map(m => `<option value="${m.id}">${m.fullName} (${m.role === "VOLUNTEER_ADMIN" ? "volunteer" : "member"})</option>`)
        .join("");
}

async function loadOpenWeek() {
    state.openWeek = await api("/api/pickup-weeks/open");
}

async function loadProducts() {
    state.products = await api("/api/products");
}

function renderBanner() {
    const banner = $("#week-banner");
    if (!state.openWeek) {
        banner.textContent = "No pickup week is currently open for orders. Please check back soon.";
        banner.classList.add("closed");
        return;
    }
    banner.innerHTML = `<strong>Open week:</strong> pickup ${state.openWeek.weekStart} to ${state.openWeek.weekEnd}
        &nbsp;·&nbsp; order deadline <strong>${state.openWeek.orderDeadline}</strong>
        &nbsp;·&nbsp; ${state.openWeek.notes || ""}`;
}

function renderProducts() {
    const list = $("#product-list");
    if (!state.products.length) {
        list.textContent = "No products available this week.";
        return;
    }
    list.innerHTML = state.products.map(p => `
        <div class="product-card">
            <h3>${p.name}</h3>
            <p class="desc">${p.description || ""}</p>
            <p><span class="price">$${money(p.price)}</span> <span class="unit">/ ${p.unit}</span></p>
            <p class="stock">${p.stockQuantity} in stock</p>
            <label>Quantity
                <input type="number" min="0" max="${p.stockQuantity}" value="0"
                       data-product="${p.id}" data-price="${p.price}"
                       onchange="updateCart(${p.id}, this.value)">
            </label>
        </div>`).join("");
}

function updateCart(productId, quantity) {
    const qty = parseInt(quantity, 10) || 0;
    if (qty <= 0) {
        delete state.cart[productId];
    } else {
        state.cart[productId] = qty;
    }
    renderCart();
}

function renderCart() {
    const ul = $("#cart-items");
    let total = 0;
    ul.innerHTML = Object.entries(state.cart).map(([pid, qty]) => {
        const p = state.products.find(x => x.id === Number(pid));
        if (!p) return "";
        const subtotal = p.price * qty;
        total += subtotal;
        return `<li><span>${p.name} × ${qty}</span><span>$${money(subtotal)}</span></li>`;
    }).join("");
    $("#cart-total").textContent = money(total);
}

async function placeOrder() {
    const msg = $("#cart-message");
    msg.classList.remove("error");
    msg.textContent = "";
    if (!state.openWeek) {
        msg.textContent = "No open pickup week.";
        msg.classList.add("error");
        return;
    }
    const memberId = $("#member-select").value;
    const items = {};
    Object.entries(state.cart).forEach(([pid, qty]) => { items[pid] = qty; });
    try {
        const order = await api("/api/orders", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ memberId: Number(memberId), pickupWeekId: state.openWeek.id, items })
        });
        msg.textContent = `Order #${order.id} placed! Total $${money(order.totalAmount)}. See you at pickup!`;
        state.cart = {};
        renderCart();
        await loadProducts();
        renderProducts();
    } catch (err) {
        msg.textContent = err.message;
        msg.classList.add("error");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    init();
    $("#place-order").addEventListener("click", placeOrder);
});
