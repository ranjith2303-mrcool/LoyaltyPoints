const api = '';

async function request(url, options = {}) {
    const response = await fetch(`${api}${url}`, {
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
        ...options
    });

    const text = await response.text();
    let data = null;
    try {
        data = text ? JSON.parse(text) : null;
    } catch {
        data = text;
    }

    if (!response.ok) {
        const message = data?.message || `Request failed with HTTP ${response.status}`;
        throw new Error(message);
    }

    return data;
}

function showMessage(text, type = 'success') {
    const el = document.getElementById('message');
    el.textContent = text;
    el.className = `message ${type}`;
    el.classList.remove('hidden');
    setTimeout(() => el.classList.add('hidden'), 3500);
}

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>'"]/g, ch => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        "'": '&#39;',
        '"': '&quot;'
    }[ch]));
}

function formatMoney(value) {
    const number = Number(value ?? 0);
    return `₹${number.toFixed(2)}`;
}

function renderPurchaseResult(data) {
    const el = document.getElementById('purchaseResult');
    const upgradeText = data.tierUpgraded
        ? `Yes — ${escapeHtml(data.previousTierName || 'Previous')} → ${escapeHtml(data.newTierName || 'New')}`
        : 'No';

    el.innerHTML = `
        <div class="result-title">Purchase Successful</div>
        <div class="result-grid">
            <div><span>Customer</span><strong>${escapeHtml(data.customerName)}</strong></div>
            <div><span>Purchase Amount</span><strong>${formatMoney(data.purchaseAmount)}</strong></div>
            <div><span>Tier</span><strong>${escapeHtml(data.tierName || 'None')}</strong></div>
            <div><span>Tier Discount</span><strong>${formatMoney(data.tierDiscount)}</strong></div>
            <div><span>Points Redeemed</span><strong>${data.pointsRedeemed ?? 0}</strong></div>
            <div><span>Points Earned</span><strong>${data.pointsEarned ?? 0}</strong></div>
            <div><span>Previous Available Points</span><strong>${data.previousAvailablePoints ?? 0}</strong></div>
            <div><span>Updated Available Points</span><strong>${data.updatedAvailablePoints ?? 0}</strong></div>
            <div><span>Final Amount</span><strong>${formatMoney(data.finalAmount)}</strong></div>
            <div><span>Tier Upgraded</span><strong>${upgradeText}</strong></div>
        </div>
    `;
    el.classList.remove('hidden');
}

function renderSummary(data) {
    const el = document.getElementById('summaryResult');
    el.innerHTML = `
        <div class="result-title">Loyalty Summary</div>
        <div class="result-grid">
            <div><span>Customer</span><strong>${escapeHtml(data.customerName)}</strong></div>
            <div><span>Email</span><strong>${escapeHtml(data.customerEmail)}</strong></div>
            <div><span>Lifetime Points</span><strong>${data.lifetimePoints ?? 0}</strong></div>
            <div><span>Available Points</span><strong>${data.availablePoints ?? 0}</strong></div>
            <div><span>Current Tier</span><strong>${escapeHtml(data.currentTierName || 'None')}</strong></div>
            <div><span>Tier Discount</span><strong>${Number(data.tierDiscountPercentage ?? 0).toFixed(0)}%</strong></div>
            <div><span>Next Tier</span><strong>${escapeHtml(data.nextTierName || 'Highest Tier')}</strong></div>
            <div><span>Points Needed</span><strong>${data.pointsNeededForNextTier ?? 0}</strong></div>
        </div>
    `;
    el.classList.remove('hidden');
}

async function loadCustomers() {
    const rows = await request('/api/customers');
    document.getElementById('customerCount').textContent = rows.length;
    document.getElementById('customerTable').innerHTML = rows.map(c => `
        <tr>
            <td>${c.id}</td>
            <td>${escapeHtml(c.name)}</td>
            <td>${escapeHtml(c.email)}</td>
            <td>${escapeHtml(c.currentTierName || 'None')}</td>
            <td>${c.availablePoints ?? 0}</td>
            <td><button onclick="loadSummary(${c.id})">View</button></td>
        </tr>
    `).join('');
}

async function loadTiers() {
    const rows = await request('/api/tiers');
    document.getElementById('tierCount').textContent = rows.length;
    document.getElementById('tierTable').innerHTML = rows.map(t => `
        <tr>
            <td>${t.id}</td>
            <td>${escapeHtml(t.name)}</td>
            <td>${t.minimumPoints}</td>
            <td>${t.discountPercentage}</td>
        </tr>
    `).join('');
}

async function loadSummary(id) {
    const data = await request(`/api/customers/${id}/loyalty`);
    renderSummary(data);
}

document.getElementById('customerForm').addEventListener('submit', async e => {
    e.preventDefault();

    try {
        await request('/api/customers', {
            method: 'POST',
            body: JSON.stringify({
                name: document.getElementById('name').value.trim(),
                email: document.getElementById('email').value.trim(),
                phone: document.getElementById('phone').value.trim() || null
            })
        });

        e.target.reset();
        showMessage('Customer created successfully.');
        await loadCustomers();
    } catch (err) {
        showMessage(err.message, 'error');
    }
});

document.getElementById('purchaseForm').addEventListener('submit', async e => {
    e.preventDefault();

    try {
        const data = await request('/api/purchases', {
            method: 'POST',
            body: JSON.stringify({
                customerId: Number(document.getElementById('purchaseCustomerId').value),
                purchaseAmount: Number(document.getElementById('purchaseAmount').value),
                pointsToRedeem: Number(document.getElementById('pointsToRedeem').value || 0)
            })
        });

        renderPurchaseResult(data);
        showMessage('Purchase processed successfully.');
        await loadCustomers();
    } catch (err) {
        const el = document.getElementById('purchaseResult');
        el.innerHTML = `<div class="result-title error-title">Purchase Failed</div><p>${escapeHtml(err.message)}</p>`;
        el.classList.remove('hidden');
        showMessage(err.message, 'error');
    }
});

document.getElementById('summaryForm').addEventListener('submit', async e => {
    e.preventDefault();

    try {
        await loadSummary(Number(document.getElementById('summaryCustomerId').value));
    } catch (err) {
        const el = document.getElementById('summaryResult');
        el.innerHTML = `<div class="result-title error-title">Unable to Load Summary</div><p>${escapeHtml(err.message)}</p>`;
        el.classList.remove('hidden');
        showMessage(err.message, 'error');
    }
});

(async function init() {
    try {
        await Promise.all([loadCustomers(), loadTiers()]);
        document.getElementById('apiStatus').textContent = 'API Connected';
    } catch (err) {
        document.getElementById('apiStatus').textContent = 'API Error';
        showMessage(err.message, 'error');
    }
})();
