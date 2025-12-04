-- Insert sample categories
INSERT INTO categories (name) VALUES
('Electronics'),
('Clothing'),
('Food'),
('Books');

-- Insert sample products
INSERT INTO products (name, description, price, stock, category_id) VALUES
('Laptop', 'High performance laptop', 999.99, 10, 1),
('Mouse', 'Wireless mouse', 29.99, 50, 1),
('T-Shirt', 'Cotton t-shirt', 19.99, 100, 2),
('Jeans', 'Blue jeans', 49.99, 75, 2),
('Apple', 'Fresh red apple', 0.99, 200, 3),
('Programming Book', 'Learn Java', 39.99, 30, 4);
