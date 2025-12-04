const API_URL = 'http://localhost:8080/api';

// Tab Management
document.querySelectorAll('.tab-button').forEach(button => {
    button.addEventListener('click', () => {
        const tabName = button.getAttribute('data-tab');
        switchTab(tabName);
    });
});

function switchTab(tabName) {
    document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
    document.getElementById(`${tabName}-tab`).classList.add('active');

    if (tabName === 'categories') {
        loadCategories();
    } else if (tabName === 'products') {
        loadProducts();
        loadCategoriesForSelect();
    }
}

// Notifications
function showNotification(message, type = 'success') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type} show`;

    setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}

// Category Management
document.getElementById('category-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('category-name').value;

    try {
        const response = await fetch(`${API_URL}/categories`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name })
        });

        if (response.ok) {
            showNotification('Categoría creada exitosamente');
            document.getElementById('category-form').reset();
            loadCategories();
        } else {
            const error = await response.json();
            showNotification(error.message || 'Error al crear categoría', 'error');
        }
    } catch (error) {
        showNotification('Error de conexión', 'error');
    }
});

async function loadCategories() {
    try {
        const response = await fetch(`${API_URL}/categories`);
        const categories = await response.json();

        const listContainer = document.getElementById('categories-list');
        listContainer.innerHTML = '';

        categories.forEach(category => {
            const item = document.createElement('div');
            item.className = 'list-item';
            item.innerHTML = `
                <div class="list-item-content">
                    <h3>${category.name}</h3>
                    <p>ID: ${category.id}</p>
                </div>
                <div class="list-item-actions">
                    <button class="btn-danger" onclick="deleteCategory(${category.id})">Eliminar</button>
                </div>
            `;
            listContainer.appendChild(item);
        });
    } catch (error) {
        showNotification('Error al cargar categorías', 'error');
    }
}

async function deleteCategory(id) {
    if (!confirm('¿Está seguro de eliminar esta categoría?')) return;

    try {
        const response = await fetch(`${API_URL}/categories/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Categoría eliminada exitosamente');
            loadCategories();
        } else {
            const error = await response.json();
            showNotification(error.message || 'Error al eliminar categoría', 'error');
        }
    } catch (error) {
        showNotification('Error de conexión', 'error');
    }
}

// Product Management
document.getElementById('product-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const productData = {
        name: document.getElementById('product-name').value,
        description: document.getElementById('product-description').value,
        price: parseFloat(document.getElementById('product-price').value),
        stock: parseInt(document.getElementById('product-stock').value),
        categoryId: parseInt(document.getElementById('product-category').value)
    };

    try {
        const response = await fetch(`${API_URL}/products`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(productData)
        });

        if (response.ok) {
            showNotification('Producto creado exitosamente');
            document.getElementById('product-form').reset();
            loadProducts();
        } else {
            const error = await response.json();
            showNotification(error.message || 'Error al crear producto', 'error');
        }
    } catch (error) {
        showNotification('Error de conexión', 'error');
    }
});

async function loadProducts(categoryId = null, search = null) {
    try {
        let url = `${API_URL}/products`;
        const params = new URLSearchParams();

        if (categoryId) params.append('categoryId', categoryId);
        if (search) params.append('search', search);

        if (params.toString()) url += `?${params.toString()}`;

        const response = await fetch(url);
        const products = await response.json();

        const listContainer = document.getElementById('products-list');
        listContainer.innerHTML = '';

        products.forEach(product => {
            const item = document.createElement('div');
            item.className = 'list-item';
            item.setAttribute('data-product-id', product.id);

            const stockBadge = product.stock < 10
                ? `<span class="badge badge-warning">Stock bajo: ${product.stock}</span>`
                : `<span class="badge badge-success">Stock: ${product.stock}</span>`;

            item.innerHTML = `
                <div class="list-item-content">
                    <h3>${product.name}</h3>
                    <p><strong>Descripción:</strong> ${product.description || 'N/A'}</p>
                    <p><strong>Precio:</strong> $${product.price.toFixed(2)}</p>
                    <p><strong>Categoría:</strong> ${product.categoryName}</p>
                    <p>${stockBadge}</p>
                </div>
                <div class="list-item-actions">
                    <button class="btn-secondary" onclick="openEditModal(${product.id})">Editar</button>
                    <button class="btn-danger" onclick="deleteProduct(${product.id})">Eliminar</button>
                </div>
            `;
            listContainer.appendChild(item);
        });
    } catch (error) {
        showNotification('Error al cargar productos', 'error');
    }
}

async function loadCategoriesForSelect() {
    try {
        const response = await fetch(`${API_URL}/categories`);
        const categories = await response.json();

        const selects = [
            document.getElementById('product-category'),
            document.getElementById('edit-product-category'),
            document.getElementById('filter-category')
        ];

        selects.forEach(select => {
            const currentValue = select.value;
            const isFilter = select.id === 'filter-category';

            select.innerHTML = isFilter
                ? '<option value="">Todas las categorías</option>'
                : '<option value="">Seleccione una categoría</option>';

            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id;
                option.textContent = category.name;
                select.appendChild(option);
            });

            if (currentValue) select.value = currentValue;
        });
    } catch (error) {
        showNotification('Error al cargar categorías', 'error');
    }
}

async function deleteProduct(id) {
    if (!confirm('¿Está seguro de eliminar este producto?')) return;

    try {
        const response = await fetch(`${API_URL}/products/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Producto eliminado exitosamente');
            loadProducts();
        } else {
            const error = await response.json();
            showNotification(error.message || 'Error al eliminar producto', 'error');
        }
    } catch (error) {
        showNotification('Error de conexión', 'error');
    }
}

// Edit Product Modal
const modal = document.getElementById('edit-product-modal');
const closeBtn = document.getElementsByClassName('close')[0];

closeBtn.onclick = function() {
    modal.style.display = 'none';
};

window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = 'none';
    }
};

async function openEditModal(id) {
    try {
        const response = await fetch(`${API_URL}/products/${id}`);
        const product = await response.json();

        document.getElementById('edit-product-id').value = product.id;
        document.getElementById('edit-product-name').value = product.name;
        document.getElementById('edit-product-description').value = product.description || '';
        document.getElementById('edit-product-price').value = product.price;
        document.getElementById('edit-product-stock').value = product.stock;
        document.getElementById('edit-product-category').value = product.categoryId;

        modal.style.display = 'block';
    } catch (error) {
        showNotification('Error al cargar producto', 'error');
    }
}

document.getElementById('edit-product-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('edit-product-id').value;
    const productData = {
        name: document.getElementById('edit-product-name').value,
        description: document.getElementById('edit-product-description').value,
        price: parseFloat(document.getElementById('edit-product-price').value),
        stock: parseInt(document.getElementById('edit-product-stock').value),
        categoryId: parseInt(document.getElementById('edit-product-category').value)
    };

    try {
        const response = await fetch(`${API_URL}/products/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(productData)
        });

        if (response.ok) {
            showNotification('Producto actualizado exitosamente');
            modal.style.display = 'none';
            loadProducts();
        } else {
            const error = await response.json();
            showNotification(error.message || 'Error al actualizar producto', 'error');
        }
    } catch (error) {
        showNotification('Error de conexión', 'error');
    }
});

// Search and Filter
document.getElementById('search-input').addEventListener('input', (e) => {
    const search = e.target.value;
    const categoryId = document.getElementById('filter-category').value;
    loadProducts(categoryId || null, search || null);
});

document.getElementById('filter-category').addEventListener('change', (e) => {
    const categoryId = e.target.value;
    const search = document.getElementById('search-input').value;
    loadProducts(categoryId || null, search || null);
});

// Initial Load
loadCategories();
