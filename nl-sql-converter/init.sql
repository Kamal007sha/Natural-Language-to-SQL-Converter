-- Create sample tables and data for NL-SQL Converter

-- Create employees table
CREATE TABLE IF NOT EXISTS employees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    city VARCHAR(50),
    department VARCHAR(50),
    salary NUMERIC(10,2),
    age INTEGER,
    hire_date DATE DEFAULT CURRENT_DATE,
    is_active BOOLEAN DEFAULT true
);

-- Create departments table
CREATE TABLE IF NOT EXISTS departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    manager_id INTEGER,
    budget NUMERIC(12,2),
    location VARCHAR(50)
);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10,2),
    category VARCHAR(50),
    stock_quantity INTEGER DEFAULT 0,
    created_date DATE DEFAULT CURRENT_DATE
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER,
    product_id INTEGER,
    quantity INTEGER,
    total_amount NUMERIC(10,2),
    order_date DATE DEFAULT CURRENT_DATE,
    status VARCHAR(20) DEFAULT 'pending'
);

-- Insert sample data into employees
INSERT INTO employees (name, email, city, department, salary, age, hire_date) VALUES
('John Doe', 'john.doe@company.com', 'Mumbai', 'Engineering', 75000.00, 30, '2022-01-15'),
('Jane Smith', 'jane.smith@company.com', 'Delhi', 'Marketing', 65000.00, 28, '2022-03-20'),
('Bob Johnson', 'bob.johnson@company.com', 'Mumbai', 'Engineering', 80000.00, 35, '2021-06-10'),
('Alice Brown', 'alice.brown@company.com', 'Bangalore', 'HR', 60000.00, 32, '2022-02-28'),
('Charlie Wilson', 'charlie.wilson@company.com', 'Mumbai', 'Sales', 70000.00, 29, '2022-04-12'),
('Diana Prince', 'diana.prince@company.com', 'Delhi', 'Engineering', 85000.00, 31, '2021-09-05'),
('Edward Davis', 'edward.davis@company.com', 'Bangalore', 'Marketing', 62000.00, 27, '2022-05-18'),
('Fiona Green', 'fiona.green@company.com', 'Mumbai', 'HR', 58000.00, 26, '2022-07-22'),
('George Miller', 'george.miller@company.com', 'Delhi', 'Sales', 72000.00, 33, '2021-12-08'),
('Helen Taylor', 'helen.taylor@company.com', 'Bangalore', 'Engineering', 78000.00, 29, '2022-01-30');

-- Insert sample data into departments
INSERT INTO departments (name, manager_id, budget, location) VALUES
('Engineering', 1, 500000.00, 'Mumbai'),
('Marketing', 2, 250000.00, 'Delhi'),
('HR', 4, 150000.00, 'Bangalore'),
('Sales', 5, 300000.00, 'Mumbai');

-- Insert sample data into products
INSERT INTO products (name, price, category, stock_quantity) VALUES
('Laptop Pro', 1200.00, 'Electronics', 50),
('Wireless Mouse', 25.00, 'Electronics', 200),
('Office Chair', 300.00, 'Furniture', 30),
('Standing Desk', 450.00, 'Furniture', 15),
('Smartphone', 800.00, 'Electronics', 100),
('Tablet', 400.00, 'Electronics', 75),
('Monitor', 250.00, 'Electronics', 60),
('Keyboard', 80.00, 'Electronics', 120),
('Desk Lamp', 45.00, 'Furniture', 40),
('Bookshelf', 150.00, 'Furniture', 25);

-- Insert sample data into orders
INSERT INTO orders (customer_id, product_id, quantity, total_amount, order_date, status) VALUES
(101, 1, 1, 1200.00, '2024-01-15', 'completed'),
(102, 2, 2, 50.00, '2024-01-16', 'completed'),
(103, 3, 1, 300.00, '2024-01-17', 'shipped'),
(104, 5, 1, 800.00, '2024-01-18', 'pending'),
(105, 7, 1, 250.00, '2024-01-19', 'completed'),
(106, 4, 1, 450.00, '2024-01-20', 'shipped'),
(107, 8, 1, 80.00, '2024-01-21', 'completed'),
(108, 6, 2, 800.00, '2024-01-22', 'pending'),
(109, 9, 3, 135.00, '2024-01-23', 'completed'),
(110, 10, 1, 150.00, '2024-01-24', 'shipped');

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_employees_city ON employees(city);
CREATE INDEX IF NOT EXISTS idx_employees_department ON employees(department);
CREATE INDEX IF NOT EXISTS idx_employees_salary ON employees(salary);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_orders_date ON orders(order_date);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO nlsql_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO nlsql_user;