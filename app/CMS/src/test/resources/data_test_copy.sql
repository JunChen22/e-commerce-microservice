-- Status Types
DROP TYPE IF EXISTS account_status_enum CASCADE;
CREATE TYPE account_status_enum AS ENUM (
	'active',
	'suspended',
	'inactive'
);

DROP TYPE IF EXISTS cart_status_enum CASCADE;
CREATE TYPE cart_status_enum AS ENUM (
	'active',
	'abandoned',
	'completed',
	'expired'
);

CREATE TYPE lifecycle_status_enum AS ENUM (
    'normal',         -- Active and functioning normally
    'soft_deleted',   -- Marked as deleted but still retained in the database
    'archived',       -- Archived but accessible for historical or reporting purposes
    'banned'          -- Banned users (could also use 'inactive' or 'deactivated')
);

DROP TYPE IF EXISTS order_status_enum CASCADE;
CREATE TYPE order_status_enum AS ENUM (
	'waiting_for_payment',
	'fulfilling',
	'sent',
	'complete',
	'closed',
	'invalid'
);

DROP TYPE IF EXISTS publish_status_enum CASCADE;
CREATE TYPE publish_status_enum AS ENUM (
	'published',
	'pending',
	'draft',
	'paused',
	'deleted'
);

DROP TYPE IF EXISTS recommendation_status_enum CASCADE;
CREATE TYPE recommendation_status_enum AS ENUM (
	'frontpage',
	'trending',
	'popular',
	'recommended',
	'new_arrival',
	'seasonal',
	'flash_sale',
	'normal',
	'hidden'
);

DROP TYPE IF EXISTS return_status_enum CASCADE;
CREATE TYPE return_status_enum AS ENUM (
	'waiting_to_be_processed',
	'returning',
	'complete',
	'rejected'
);

DROP TYPE IF EXISTS verification_status_enum CASCADE;
CREATE TYPE verification_status_enum AS ENUM (
	'verified',
	'not_verified'
);


-- Type Enums

DROP TYPE IF EXISTS discount_type_enum CASCADE;
CREATE TYPE discount_type_enum AS ENUM (
	'amount',
	'percent'
);

DROP TYPE IF EXISTS email_service_type_enum CASCADE;
CREATE TYPE email_service_type_enum AS ENUM (
    'user_service',
    'user_service_all',
    'sale_service',
    'order_service',
    'order_service_update',
    'order_service_return',
    'order_service_return_update'
);

DROP TYPE IF EXISTS payment_type_enum CASCADE;
CREATE TYPE payment_type_enum AS ENUM (
	'credit_card',
	'paypal',
	'google_pay'
);

DROP TYPE IF EXISTS sales_type_enum CASCADE;
CREATE TYPE sales_type_enum AS ENUM (
    'not_on_sale',
    'is_on_sale',
    'flash_sale',
    'special_sales',
    'clearance',
    'used_item'
);

DROP TYPE IF EXISTS source_type_enum CASCADE;
CREATE TYPE source_type_enum AS ENUM (
    'web',
    'mobile'
);

DROP TYPE IF EXISTS target_type_enum CASCADE;
CREATE TYPE target_type_enum AS ENUM (
	'all',
	'specific_brand',
	'specific_category',
	'specific_item'
);

-- etc
DROP TYPE IF EXISTS product_condition_enum CASCADE;
CREATE TYPE product_condition_enum AS ENUM (
	'new',
	'used',
	'refurbished'
);


-------------------
----- PMS ---------
-------------------
DROP TABLE IF EXISTS brand CASCADE;
CREATE TABLE brand (
    id SERIAL PRIMARY KEY,
    name TEXT,
    slug TEXT UNIQUE,  -- Add slug for SEO-friendly URLs
    logo TEXT,
    publish_status publish_status_enum DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO brand(name, slug, logo, publish_status)
VALUES
-- phone/computer/electronic brand
('Apple', 'apple', 'apple.jpg', 'published'),
('Samsung', 'samsung', 'samsung.jpg', 'published'),
('Google', 'google', 'google.jpg', 'published'),
('OnePlus', 'oneplus', 'OnePlus.jpg', 'published'),
('Lenovo', 'lenovo', 'lenovo.jpg', 'published'),
('ASUS', 'asus', 'asus.jpg', 'published'),
('Acer', 'acer', 'acer.jpg', 'published'),
('Alienware', 'alienware', 'alienware.jpg', 'published'),
('Razer', 'razer', 'razer.jpg', 'published'),
('Microsoft', 'microsoft', 'microsoft.jpg', 'published'),
('Dell', 'dell', 'dell.jpg', 'published'),
('HP', 'hp', 'hp.jpg', 'published'),
('MSI', 'msi', 'msi.jpg', 'published'),

-- electrics
('Anker', 'anker', 'anker.jpg', 'published'),
('Fitbit', 'fitbit', 'fitbit.jpg', 'published'),
('SanDisk', 'sandisk', 'sandisk.jpg', 'published'),
('Tile', 'tile', 'tile.jpg', 'published'),

-- video and audio
('GoPro', 'gopro', 'gopro.jpg', 'published'),
('Logitech', 'logitech', 'logitech.jpg', 'published'),
('JBL', 'jbl', 'jbl.jpg', 'published'),
('UE', 'ue', 'ue.jpg', 'published'),

-- clothing brand
('Gucci', 'gucci', 'gucci.jpg', 'published'),
('Nike', 'nike', 'nike.jpg', 'published'),
('Adidas', 'adidas', 'adidas.jpg', 'published'),
('Zara', 'zara', 'zara.jpg', 'published'),
('HM', 'hm', 'hm.jpg', 'published'),
('Levis', 'levis', 'levis.jpg', 'published'),
('Calvin Klein', 'calvin-klein', 'calvinklein.jpg', 'published'),
('Versace', 'versace', 'versace.jpg', 'published'),

-- health and beauty brand
('Nivea', 'nivea', 'nivea.jpg', 'published'),
('Dove', 'dove', 'dove.jpg', 'published'),
('Cetaphil', 'cetaphil', 'cetaphil.jpg', 'published'),
('Neutrogena', 'neutrogena', 'neutrogena.jpg', 'published'),
('Aveeno', 'aveeno', 'aveeno.jpg', 'published'),
('Olay', 'olay', 'olay.jpg', 'published'),

-- books
('Penguin Books', 'penguin-books', 'penguinbooks.jpg', 'published'),
('HarperCollins', 'harpercollins', 'harpercollins.jpg', 'published'),
('Random House', 'random-house', 'randomhouse.jpg', 'published'),
('Book', 'book', 'book.jpg', 'published'),

-- etc
('Coca-Cola', 'coca-cola', 'cocacola.jpg', 'published'),

-- kitchen
('Zojirushi', 'zojirushi', 'zojirushi.jpg', 'published'),
('Yeti', 'yeti', 'yeti.jpg', 'published');

DROP TABLE IF EXISTS brand_update_log;
CREATE TABLE brand_update_log (
    id SERIAL PRIMARY KEY,
    brand_id INTEGER NOT NULL,
    update_action TEXT NOT NULL,
    operator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO brand_update_log (brand_id, update_action, operator)
VALUES
(1, 'Update', 'John Doe'),
(2, 'Create', 'Alice Smith'),
(3, 'Delete', 'Bob Johnson'),
(4, 'Update', 'Eve Wilson'),
(5, 'Create', 'Charlie Brown'),
(6, 'Update', 'Grace Davis'),
(7, 'Delete', 'Frank Miller'),
(8, 'Create', 'Lucy Adams'),
(9, 'Update', 'David Clark'),
(10, 'Create', 'Sarah White');


DROP TABLE IF EXISTS product_category CASCADE;
CREATE TABLE product_category (
    id SERIAL PRIMARY KEY,
    name TEXT,
    parent_id INTEGER NOT NULL,-- sub category
    level INTEGER
);

-- main category
INSERT INTO product_category(name, parent_id, level) VALUES
('Fashion', 0, 1),
('Books, movies and music', 0, 1),
('Electronics', 0, 1),
('Home & garden', 0, 1),
('Sporting goods', 0, 1),
('Health and beauty', 0, 1);

--- sub category
INSERT INTO product_category(name, parent_id, level) VALUES
-- Fashion
('Men clothing', 1, 2),
('Men shoes', 1, 2),
('Women shoes', 1, 2),
('Women clothing', 1, 2),
('Women accessories', 1, 2),

-- Books, Movies and Music
('DVDs', 2, 2),
('Books', 2, 2),
('Instrument', 2, 2),

 -- Electronics
('Smartphones', 3, 2),
('Desktop', 3, 2),
('Laptop', 3, 2),
('Video games and consoles', 3, 2),
('Tablets', 3, 2),
('Wearables', 3, 2),
('Headphones', 3, 2),
('Storage', 3, 2),
('Monitors', 3, 2),

-- Home & Garden
('Power tools', 4, 2),
('Outdoor', 4, 2),
('Kitchen', 4, 2),

-- Sporting Goods
('Outdoor sports', 5, 2),
('Team sports', 5, 2),
('Fitness', 5, 2),

 -- Health and Beauty
('Vitamins', 6, 2),
('Skin care', 6, 2),
('Health care products', 6, 2);


DROP TABLE IF EXISTS product_attribute_category;
CREATE TABLE product_attribute_category (
    id SERIAL PRIMARY KEY,
    name TEXT,                     -- name of category. For example phone, shoes,
    attribute_amount INTEGER      -- number of attribute for this, phone have 6 attribute, storage capacity, screen size, battery capapcity, etc
);


INSERT INTO product_attribute_category (name, attribute_amount)
VALUES
-- category -> Fashion - >
('Men clothing', 3),
('Men shoes', 3),
('Women shoes', 3),
('Women clothing', 3),
('Women accessories', 3),

-- category -> Books, Movies and Music - >
('DVDs', 1),
('Books', 3),
('Instrument', 2),

-- category -> Electronics - >
('Smartphones', 10),
('Desktop', 8),
('Laptop', 10),
('Video games and consoles', 4),
('Tablets', 10),
('Wearables', 10),
('Headphones', 4),
('Storage', 5),
('Monitors', 7),

-- category -> Home & Garden - >
('Power tools', 2),
('Outdoor', 1),
('Kitchen', 1),

-- category -> Sporting Goods - >
('Outdoor sports', 1),
('Team sports', 1),
('Fitness', 1),

-- category -> Health and Beauty - >
('Vitamins', 1),
('Skin care', 1),
('Health care products', 1);


DROP TABLE IF EXISTS product_attribute_type;
CREATE TABLE product_attribute_type (
    id SERIAL PRIMARY KEY,
    attribute_category_id INTEGER NOT NULL,
    name TEXT
);

INSERT INTO product_attribute_type (attribute_category_id, name)
VALUES

-- Men clothing
(1, 'size'),
(1, 'color'),
(1, 'style'),

-- Men shoes
(2, 'size'),
(2, 'color'),
(2, 'style'),

-- Women shoes
(3, 'size'),
(3, 'color'),
(3, 'style'),

-- Women clothing
(4, 'size'),
(4, 'color'),
(4, 'style'),

-- Women accessories
(5, 'size'),
(5, 'color'),
(5, 'style'),

-- DVDs
(6, 'movie type'),   -- action, comedy, romance and etc

-- Books
(7, 'ISBN'),
(7, 'length'),
(7, 'language'),

-- Instrument
(8, 'material'),
(8, 'type'),  -- guitar, piano, jazz

-- Smartphones
(9, 'storage capacity'),
(9, 'camera resolution'),
(9, 'screen size'),
(9, 'ram size'),
(9, 'screen resolution'),
(9, 'battery capacity'),
(9, 'processor'),
(9, 'color'),
(9, 'ports'),
(9, 'year model'),

-- Desktop
(10, 'storage capacity'),
(10, 'ram size'),
(10, 'cooling type'),
(10, 'graphic card'),
(10, 'processor'),
(10, 'case'),
(10, 'ports'),
(10, 'year model'),

-- Laptop
(11, 'storage capacity'),
(11, 'screen size'),
(11, 'ram size'),
(11, 'screen resolution'),
(11, 'battery capacity'),
(11, 'processor'),
(11, 'color'),
(11, 'keyboard language'),
(11, 'ports'),
(11, 'year model'),

-- Video games and consoles
(12, 'game type'),    -- action, horror, FPS, RPG and etc
(12, 'console type'),
(12, 'ports'),
(12, 'year model'),

-- Tablets
(13, 'storage capacity'),
(13, 'camera resolution'),
(13, 'screen size'),
(13, 'ram size'),
(13, 'screen resolution'),
(13, 'battery capacity'),
(13, 'processor'),
(13, 'color'),
(13, 'ports'),
(13, 'year model'),

-- Wearables
(14, 'storage capacity'),
(14, 'camera resolution'),
(14, 'screen size'),
(14, 'ram size'),
(14, 'screen resolution'),
(14, 'battery capacity'),
(14, 'processor'),
(14, 'color'),
(14, 'ports'),
(14, 'year model'),

-- Headphones
(15, 'color'),
(15, 'connection type'),     -- wired, wireless
(15, 'ports'),
(15, 'year model'),

-- Storage
(16, 'storage type'),     -- NVME, SATA, HDD, USB flash drive, or tape
(16, 'connection port'),  -- USB-A, USB-C, m.2, SATA connection and etc
(16, 'storage size'),
(16, 'storage speed'),    -- read/write speed
(16, 'year model'),

-- Monitors
(17, 'screen size'),
(17, 'screen resolution'),
(17, 'screen refresh rate'),
(17, 'color'),
(17, 'speaker'),
(17, 'ports'),
(17, 'year model'),

-- Power tools
(18, 'battery capacity'),
(18, 'power(watts)'),

-- Outdoor
(19, 'dimension L x W x H'),

-- Kitchen
(20, 'dimension L x W x H'),

-- Outdoor sports
(21, 'sport type'),

-- Team sports
(22, 'sport type'),

-- Fitness
(23, 'dimension L x W x H'),

-- Vitamins
(24, 'dimension L x W x H'),

-- Skin care
(25, 'dimension L x W x H'),

-- Health care products
(26, 'dimension L x W x H');


DROP TABLE IF EXISTS product CASCADE;
CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    brand_id INTEGER NOT NULL REFERENCES brand(id),
    brand_name TEXT,
    name TEXT NOT NULL,
    slug TEXT UNIQUE, -- for SEO friendly URL
    category_id INTEGER NOT NULL REFERENCES product_category(id),
    category_name TEXT,
    attribute_category_id INTEGER NOT NULL,
    sn VARCHAR(64) UNIQUE, -- serial number
    condition_status product_condition_enum DEFAULT 'new',
    recommend_status recommendation_status_enum DEFAULT 'normal',
    verify_status verification_status_enum default 'not_verified',
    sub_title TEXT,
    cover_picture TEXT,           --  preview picture, for like list all, search all picture when getting specific
    picture_album INTEGER,           -- collection of picture
    description TEXT,
    original_price DECIMAL(10, 2),
    on_sale_status INTEGER DEFAULT 0,  --  0 -> not on sale; 1 -> is on sale; 2 -> flash sale/special sales/clarance/used item
    sale_price DECIMAL(10, 2),     -- TODO: currently using it as lowest price of all sku variants. and using original price as highest, it changes with more sku variants added.
    stock INTEGER,
    low_stock INTEGER, -- -- low stock alarm, default is about 10% alarm
    unit_sold INTEGER,
    weight DECIMAL(10,2), -- product weight in grams
    keywords TEXT,
    detail_title TEXT,                -- at the bottom of product with detail title, description and picture
    detail_desc TEXT,
    description_album_id INTEGER,
    delete_status BOOLEAN DEFAULT FALSE , -- soft delete, 0 -> product not deleted; 1 -> product deleted, record purpose
    publish_status publish_status_enum DEFAULT 'pending',
    lifecycle_status lifecycle_status_enum DEFAULT 'normal',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    note TEXT
);

-- Insert data into the product table
INSERT INTO product (brand_id, brand_name, name, slug, category_id, category_name, attribute_category_id, sn, sub_title, cover_picture, picture_album, description, original_price, on_sale_status, sale_price, stock, low_stock, unit_sold, weight, keywords, detail_title, detail_desc, description_album_id, publish_status)
VALUES
-- electronics
(1, 'Apple', 'iPhone 12', 'iphone-12', 15, 'Smartphones', 9, 'SN-123', 'Powerful and sleek', 'iphone12.jpg', 1, 'The iPhone 12 is the latest flagship smartphone from Apple.', 899.99, 0, 899.99, 100, 10, 50, 150, 'Apple, iPhone, smartphone', 'Product Details', 'Explore the amazing features of the iPhone 12.', 2, 'published'),
(1, 'Apple', 'iPhone SE', 'iphone-se', 15, 'Smartphones', 9, 'SN-456', 'Compact and affordable', 'iphoneSE.jpg', 3, 'The iPhone SE packs power and performance into a compact design.', 499.99, 0, 499.99, 50, 5, 30, 120, 'Apple, iPhone, smartphone', 'Product Details', 'Discover the capabilities of the iPhone SE.', 4, 'published'),
(3, 'Google', 'Pixel 5', 'pixel-5', 15, 'Smartphones', 9, 'SN-345', 'Capture the perfect shot', 'pixel5.jpg', 5, 'The Pixel 5 features an exceptional camera and advanced AI capabilities.', 799.99, 0, 799.99, 80, 8, 40, 140, 'Google, Pixel, smartphone', 'Product Details', 'Capture stunning photos with the Pixel 5.', 6, 'published'),
(4, 'OnePlus', 'OnePlus 9 Pro', 'oneplus-9-pro', 15, 'Smartphones', 9, 'SN-789', 'Flagship performance', 'oneplus9Pro.jpg', 7, 'The OnePlus 9 Pro delivers exceptional performance and photography capabilities.', 1099.99, 0, 1099.99, 100, 10, 60, 180, 'OnePlus, smartphone', 'Product Details', 'Experience flagship performance with the OnePlus 9 Pro.', 8, 'published'),
(2, 'Samsung', 'Galaxy S21', 'galaxy-s21', 15, 'Smartphones', 9, 'SN-234', 'Powerful and feature-rich', 'galaxyS21.jpg', 9, 'The Galaxy S21 offers cutting-edge technology and a stunning display.', 1099.99, 0, 1099.99, 150, 15, 70, 170, 'Samsung, Galaxy, smartphone', 'Product Details', 'Experience the brilliance of the Galaxy S21.', 10, 'published'),

(1, 'Apple', 'AirPods Pro', 'airpods-pro', 21, 'Headphones', 15, 'SN-235', 'Immersive sound, active noise cancellation', 'airPodsPro.jpg', 11, 'The AirPods Pro deliver immersive sound and feature active noise cancellation for an enhanced audio experience.', 249.99, 0, 249.99, 200, 20, 150, 40, 'Apple, AirPods, headphones', 'Product Details', 'Enjoy immersive sound with the AirPods Pro.', 12, 'published'),
(1, 'Apple', 'AirPods 2', 'airpods-2', 21, 'Headphones', 15, 'SN-782', 'Immersive audio experience', 'airpods2.jpg', 13, 'The AirPods 2 deliver superior sound quality with active noise cancellation.', 249.99, 0, 249.99, 200, 20, 100, 50, 'Apple, AirPods, headphones', 'Product Details', 'Enjoy immersive audio with the AirPods Pro.', 14, 'published'),
(4, 'OnePlus', 'OnePlus Buds Pro', 'oneplus-buds-pro', 21, 'Headphones', 15, 'SN-012', 'Immersive audio experience', 'oneplusBudsPro.jpg', 15, 'The OnePlus Buds Pro offer immersive sound and advanced noise cancellation.', 149.99, 0, 149.99, 150, 15, 90, 30, 'OnePlus, earbuds, headphones', 'Product Details', 'Enjoy immersive audio with the OnePlus Buds Pro.', 16, 'published'),

(1, 'Apple', 'iPad Pro', 'ipad-pro', 19, 'Tablets', 13, 'SN-901', 'The ultimate iPad experience', 'iPadPro.jpg', 17, 'The iPad Pro offers the ultimate tablet experience with its powerful performance and stunning display.', 799.99, 0, 799.99, 150, 15, 100, 470, 'Apple, iPad, tablet', 'Product Details', 'Experience the ultimate tablet experience with the iPad Pro.', 18, 'published'),

(4, 'OnePlus', 'OnePlus Watch', 'oneplus-watch', 20, 'Wearables', 14, 'SN-342', 'Stylish and smart', 'oneplusWatch.jpg', 19, 'The OnePlus Watch combines style and smart features for a seamless wearable experience.', 199.99, 0, 199.99, 80, 8, 50, 60, 'OnePlus, smartwatch', 'Product Details', 'Stay connected with the OnePlus Watch.', 20, 'published'),
(2, 'Samsung', 'Galaxy Watch 3', 'galaxy-watch-3', 20, 'Wearables', 14, 'SN-567', 'Stylish and functional', 'galaxyWatch3.jpg', 21, 'The Galaxy Watch 3 combines style and functionality for a smart wearable experience.', 349.99, 0, 349.99, 100, 10, 50, 90, 'Samsung, Galaxy, smartwatch', 'Product Details', 'Stay connected with the Galaxy Watch 3.', 22, 'published'),

(2, 'Samsung', 'T5 Portable SSD', 't5-portable-ssd', 22, 'Storage', 16, 'SN-890', 'Fast and portable storage', 't5PortableSSD.jpg', 23, 'The T5 Portable SSD offers fast and secure storage for your data on the go.', 179.99, 0, 179.99, 250, 25, 120, 200, 'Samsung, SSD, storage', 'Product Details', 'Store and transfer your data with the T5 Portable SSD.', 24, 'published'),

(1, 'Apple', 'MacBook Pro', 'macbook-pro', 17, 'Laptop', 11, 'SN-678', 'Powerful performance, sleek design', 'macBookPro.jpg', 25, 'The MacBook Pro combines powerful performance with a sleek design, making it the perfect choice for professionals.', 1999.99, 0, 1999.99, 80, 8, 40, 170, 'Apple, MacBook, laptop', 'Product Details', 'Unleash your creativity with the MacBook Pro.', 26, 'published'),
(11, 'Dell', 'XPS 13', 'xps-13', 17, 'Laptop', 11, 'SN-452', 'Compact and powerful', 'xps13.jpg', 27, 'The XPS 13 is a compact and powerful laptop that delivers exceptional performance in a sleek design.', 1399.99, 0, 1399.99, 150, 15, 90, 110, 'Dell, XPS, laptop', 'Product Details', 'Experience power and portability with the XPS 13.', 28, 'published'),
(5, 'Lenovo', 'ThinkPad X1 Carbon', 'thinkpad-x1-carbon', 17, 'Laptop', 11, 'SN-125', 'Powerful and lightweight', 'thinkpadX1Carbon.jpg', 29, 'The ThinkPad X1 Carbon is a powerful and lightweight laptop designed for professionals.', 1499.99, 0, 1499.99, 200, 20, 100, 120, 'Lenovo, ThinkPad, laptop', 'Product Details', 'Experience the power of the ThinkPad X1 Carbon.', 30, 'published'),
(5, 'Lenovo', 'Yoga C940', 'yoga-c940', 17, 'Laptop', 11, 'SN-453', 'Versatile and stylish', 'yogaC940.jpg', 31, 'The Yoga C940 is a versatile and stylish 2-in-1 laptop with impressive performance.', 1299.99, 0, 1299.99, 150, 15, 80, 140, 'Lenovo, Yoga, laptop', 'Product Details', 'Unleash your creativity with the Yoga C940.', 32, 'published'),
(5, 'Lenovo', 'IdeaPad Gaming 3', 'ideapad-gaming-3', 17, 'Laptop', 11, 'SN-785', 'Powerful gaming performance', 'ideapadGaming3.jpg', 33, 'The IdeaPad Gaming 3 is a powerful gaming laptop that delivers exceptional performance.', 999.99, 0, 999.99, 100, 10, 50, 180, 'Lenovo, IdeaPad, gaming laptop', 'Product Details', 'Immerse yourself in the world of gaming with the IdeaPad Gaming 3.', 34, 'published'),
(8, 'Alienware', 'Alienware m15 R5', 'alienware-m15-r5', 17, 'Laptop', 11, 'SN-902', 'Unleash gaming supremacy', 'alienwarem15R5.jpg', 35, 'The Alienware m15 R5 is a gaming laptop that delivers unrivaled gaming performance.', 1999.99, 0, 1999.99, 80, 8, 50, 230, 'Alienware, gaming laptop', 'Product Details', 'Experience gaming supremacy with the Alienware m15 R5.', 36, 'published'),

(10, 'Microsoft', 'Xbox Series X', 'xbox-series-x', 18, 'Video Games and Consoles', 12, 'SN-126', 'Next-generation gaming', 'xboxSeriesX.jpg', 37, 'The Xbox Series X offers next-generation gaming with its powerful performance and immersive gaming experiences.', 499.99, 0, 499.99, 80, 8, 50, 4000, 'Microsoft, Xbox, gaming console', 'Product Details', 'Enter the next generation of gaming with the Xbox Series X.', 38, 'published'),

(11, 'Dell', 'Dell UltraSharp U2720Q', 'dell-ultrasharp-u2720q', 23, 'Monitors', 17, 'SN-016', 'Exceptional color accuracy', 'dellUltraSharpU2720Q.jpg', 39, 'The Dell UltraSharp U2720Q is a professional-grade monitor that offers exceptional color accuracy for precise image reproduction.', 599.99, 0, 599.99, 120, 12, 70, 630, 'Dell, UltraSharp, monitor', 'Product Details', 'Experience exceptional color accuracy with the Dell UltraSharp U2720Q.', 40, 'published'),

-- sneakers/shoes
(23, 'Nike', 'Nike Air Max 270', 'nike-air-max-270', 8, 'Men shoes', 2, 'SN-001', 'Iconic design and comfort', 'nikeAirMax270.jpg', 41, 'Experience iconic design and unmatched comfort with the Nike Air Max 270 sneakers.', 129.99, 0, 129.99, 100, 10, 50, 500, 'Nike, Air Max, sneakers', 'Product Details', 'Step up your style game with the Nike Air Max 270.', 42, 'published'),
(23, 'Nike', 'Nike ZoomX Vaporfly NEXT', 'nike-zoomx-vaporfly-next', 8, 'Men shoes', 2, 'SN-003', 'Unmatched speed and performance', 'nikeZoomXVaporfly.jpg', 43, 'The Nike ZoomX Vaporfly NEXT% provides unmatched speed and performance for professional runners.', 249.99, 0, 249.99, 80, 8, 30, 350, 'Nike, ZoomX Vaporfly, sneakers', 'Product Details', 'Take your running to the next level with the Nike ZoomX Vaporfly NEXT.', 44, 'published'),
(24, 'Adidas', 'Adidas Ultra Boost', 'adidas-ultra-boost', 8, 'Men shoes', 2, 'SN-004', 'Ultimate comfort and style', 'adidasUltraBoost.jpg', 45, 'Experience ultimate comfort and style with the Adidas Ultra Boost sneakers.', 149.99, 0, 149.99, 150, 15, 60, 450, 'Adidas, Ultra Boost, sneakers', 'Product Details', 'Elevate your sneaker game with the Adidas Ultra Boost.', 46, 'published'),
(24, 'Adidas', 'Adidas Adilette Slides', 'adidas-adilette-slides', 9, 'Women shoes ', 3, 'SN-006', 'Casual and comfortable', 'adidasAdiletteSlides.jpg', 47, 'The Adidas Adilette Slides are casual and comfortable sandals perfect for lounging or post-workout relaxation.', 29.99, 0, 29.99, 200, 20, 120, 150, 'Adidas, Adilette, slides, sandals', 'Product Details', 'Slip into comfort with the Adidas Adilette Slides.', 48, 'published'),

-- clothing
(23, 'Nike', 'Nike Dri-FIT T-Shirt', 'nike-dri-fit-t-shirt', 7, 'Men clothing', 1, 'SN-002', 'Stay cool and comfortable', 'nikeDriFitShirt.jpg', 49, 'The Nike Dri-FIT T-Shirt keeps you cool and comfortable during your workouts or everyday activities.', 29.99, 0, 29.99, 200, 20, 100, 200, 'Nike, Dri-FIT, t-shirt', 'Product Details', 'Upgrade your wardrobe with the Nike Dri-FIT T-Shirt.', 50, 'published'),
(29, 'Calvin Klein', 'Calvin Klein Logo T-Shirt', 'calvin-klein-logo-t-shirt', 7, 'Men clothing', 1, 'SN-008', 'Classic and timeless', 'calvinKleinLogoShirt.jpg', 51, 'The Calvin Klein Logo T-Shirt features a classic and timeless design that adds style to any outfit.', 39.99, 0, 39.99, 100, 10, 70, 250, 'Calvin Klein, logo t-shirt, clothing', 'Product Details', 'Make a statement with the Calvin Klein Logo T-Shirt.', 52, 'published'),
(24, 'Adidas', 'Adidas Essential Track Pants', 'adidas-essential-track-pants', 7, 'Men clothing', 1, 'SN-005', 'Sporty and versatile', 'adidasTrackPants.jpg', 53, 'The Adidas Essential Track Pants offer a sporty and versatile option for your everyday activities.', 49.99, 0, 49.99, 100, 10, 80, 300, 'Adidas, track pants, clothing', 'Product Details', 'Stay comfortable and stylish with the Adidas Essential Track Pants.', 54, 'published'),

-- Books
(40, 'Books', 'The Great Gatsby', 'the-great-gatsby', 13, 'Books', 7, 'SN-010', 'A classic tale of wealth and obsession', 'greatGatsby.jpg', 55, 'The Great Gatsby is a classic novel that explores themes of wealth, love, and the American Dream.', 14.99, 0, 14.99, 200, 20, 150, 300, 'The Great Gatsby, novel, literature', 'Product Details', 'Immerse yourself in the world of The Great Gatsby.', 56, 'published'),
(40, 'Books', 'To Kill a Mockingbird', 'to-kill-a-mockingbird', 13, 'Books', 7, 'SN-011', 'A powerful story of racial injustice and compassion', 'toKillAMockingbird.jpg', 57, 'To Kill a Mockingbird is a powerful novel that addresses themes of racial injustice and the power of compassion.', 12.99, 0, 12.99, 150, 15, 120, 250, 'To Kill a Mockingbird, novel, literature', 'Product Details', 'Experience the impact of To Kill a Mockingbird.', 58, 'published'),
(40, 'Books', 'Harry Potter and the Sorcerer''s Stone', 'harry-potter-and-the-sorcerers-stone', 13, 'Books', 7, 'SN-017', 'The beginning of a magical journey', 'harryPotterSorcerersStone.jpg', 59, 'Harry Potter and the Sorcerer''s Stone is the first book in the Harry Potter series, introducing readers to the magical world of Hogwarts.', 19.99, 0, 19.99, 100, 10, 200, 400, 'Harry Potter, Sorcerer''s Stone, fantasy, literature', 'Product Details', 'Embark on a magical journey with Harry Potter and the Sorcerer''s Stone.', 60, 'published');


-- A product SKU (Stock Keeping Unit) is a unique identifier assigned to a specific product variant to facilitate inventory management, tracking, and sales analysis.
DROP TABLE IF EXISTS product_sku;
CREATE TABLE product_sku (    -- all product have one default sku variant
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    sku_code TEXT UNIQUE,  -- used like a slug, unique for each product
    picture TEXT,
    price DECIMAL(10, 2),
    promotion_price DECIMAL(10, 2),
    stock INTEGER,
    low_stock INTEGER,     -- low stock alarm, default is about 10% alarm
    lock_stock INTEGER DEFAULT 0, -- lock stock is updated from lock stock + order quantity, can't order when current stock is less than lock stock. update lock stock to 0 after ordered.
    unit_sold INTEGER,
    publish_status publish_status_enum DEFAULT 'pending',
    lifecycle_status lifecycle_status_enum DEFAULT 'normal',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO product_sku (product_id, sku_code, picture, price, promotion_price, stock, low_stock, unit_sold)
VALUES
-- iphone/electronics
(1, 'IP12-RED-128', 'iphone12-red.jpg', 899.99, 899.99, 35, 4, 20),
(1, 'IP12-WHITE-128', 'iphone12-white.jpg', 899.99, 899.99, 35, 4, 20),
(1, 'IP12-BLACK-128', 'iphone12-black.jpg', 899.99, 899.99, 30, 2, 10),
(2, 'IPSE-BLUE-64', 'iphonese-blue.jpg', 499.99, 499.99, 25, 3, 25),
(2, 'IPSE-RED-64', 'iphonese-red.jpg', 499.99, 499.99, 25, 3, 5),
(3, 'PX5', 'pixel5.jpg', 799.99, 799.99, 80, 8, 40),
(4, 'OP9P', 'oneplus9pro.jpg', 1099.99, 1099.99, 100, 10, 60),
(5, 'GS21', 'galaxys21.jpg', 1099.99, 1099.99, 150, 15, 70),
(6, 'APRO1', 'airpodspro.jpg', 249.99, 249.99, 200, 20, 150),
(7, 'APO2', 'airpods2.jpg', 249.99, 249.99, 200, 20, 100),
(8, 'OBPRO', 'oneplusbudspro.jpg', 149.99, 149.99, 150, 15, 90),
(9, 'IPPRO', 'ipadpro.jpg', 799.99, 799.99, 150, 15, 100),
(10, 'OPW3', 'onepluswatch.jpg', 199.99, 199.99, 80, 8, 50),
(11, 'GW3', 'galaxywatch3.jpg', 349.99, 349.99, 100, 10, 50),
(12, 'T5SSD', 't5portablessd.jpg', 179.99, 179.99, 250, 25, 120),
(13, 'MBP', 'macbookpro.jpg', 1999.99, 1999.99, 80, 8, 40),
(14, 'XPS13', 'xps13.jpg', 1399.99, 1399.99, 150, 15, 90),
(15, 'TPX1C', 'thinkpadx1carbon.jpg', 1499.99, 1499.99, 200, 20, 100),
(16, 'YC940', 'yogac940.jpg', 1299.99, 1299.99, 150, 15, 80),
(17, 'IPG3', 'ideapadgaming3.jpg', 999.99, 999.99, 100, 10, 50),
(18, 'AM15R5', 'alienwarem15r5.jpg', 1999.99, 1999.99, 80, 8, 50),
(19, 'XSX', 'xboxseriesx.jpg', 499.99, 499.99, 80, 8, 50),
(20, 'DUU2720Q', 'dellultrasharp.jpg', 599.99, 599.99, 120, 12, 70),
-- clothing and shoes
(21, 'NAM270-105', 'nikeairmax270.jpg', 129.99, 129.99, 40, 10, 50),
(21, 'NAM270-90', 'nikeairmax270.jpg', 129.99, 119.99, 30, 10, 50),
(21, 'NAM270-75', 'nikeairmax270.jpg', 129.99, 109.99, 30, 10, 50),

(22, 'NZVNEXT', 'nikezoomx.jpg', 249.99, 249.99, 80, 8, 30),
(23, 'AUB', 'adidasultraboost.jpg', 149.99, 149.99, 150, 15, 60),
(24, 'AAS', 'adidasadilette.jpg', 29.99, 29.99, 200, 20, 120),
(25, 'NDFTS', 'nikedrifit.jpg', 29.99, 29.99, 200, 20, 100),

(26, 'CKLTS-L', 'calvinkleintshirt.jpg', 39.99, 39.99, 100, 10, 70),
(26, 'CKLTS-M', 'calvinkleintshirt.jpg', 39.99, 39.99, 100, 10, 70),
(26, 'CKLTS-S', 'calvinkleintshirt.jpg', 39.99, 39.99, 100, 10, 70),

(27, 'AEPTP', 'adidasessentialpants.jpg', 49.99, 49.99, 100, 10, 80),

(28, 'TGG', 'thegreatgatsby.jpg', 14.99, 14.99, 200, 20, 150),
(29, 'TKAM', 'tokillamockingbird.jpg', 12.99, 12.99, 150, 15, 120),
(30, 'HPSS', 'harrypotter.jpg', 19.99, 19.99, 100, 10, 200);


DROP TABLE IF EXISTS product_attribute;
CREATE TABLE product_attribute (
    id SERIAL PRIMARY KEY,
    sku_code TEXT,
    product_id INTEGER NOT NULL,
    attribute_type_id INTEGER NOT NULL,
    attribute_value TEXT,
    attribute_unit TEXT
);

INSERT INTO product_attribute (product_id, sku_code, attribute_type_id, attribute_value, attribute_unit)
VALUES
-- iPhone 12 and variant(red, white, black)
(1, 'IP12-RED-128', 22, '128', 'GB'), -- storage capacity
(1, 'IP12-RED-128', 23, '12', 'MP'), -- camera resolution
(1, 'IP12-RED-128', 24, '6.1', 'inches'), -- screen size
(1, 'IP12-RED-128', 25, '4 ', 'GB'), -- RAM size
(1, 'IP12-RED-128', 26, '2532 x 1170', 'pixels'), -- screen resolution
(1, 'IP12-RED-128', 27, '2815', 'mAh'), -- battery capacity
(1, 'IP12-RED-128', 28, 'A14', 'Bionic'), -- processor
(1, 'IP12-RED-128', 29, 'Red', NULL), -- color
(1, 'IP12-RED-128', 30, 'Lightning', NULL), -- ports
(1, 'IP12-RED-128', 31, '2024', NULL), -- year

(1, 'IP12-WHITE-128', 22, '128', 'GB'), -- storage capacity
(1, 'IP12-WHITE-128', 23, '12', 'MP'), -- camera resolution
(1, 'IP12-WHITE-128', 24, '6.1', 'inches'), -- screen size
(1, 'IP12-WHITE-128', 25, '4', 'GB'), -- RAM size
(1, 'IP12-WHITE-128', 26, '2532 x 1170', 'pixels'), -- screen resolution
(1, 'IP12-WHITE-128', 27, '2815', 'mAh'), -- battery capacity
(1, 'IP12-WHITE-128', 28, 'Apple A14 Bionic', NULL), -- processor
(1, 'IP12-WHITE-128', 29, 'White', NULL), -- color
(1, 'IP12-WHITE-128', 30, 'Lightning', NULL), -- ports
(1, 'IP12-WHITE-128', 31, '2024', NULL), -- year

(1, 'IP12-BLACK-128', 22, '128', 'GB'), -- storage capacity
(1, 'IP12-BLACK-128', 23, '12 MP', 'MP'), -- camera resolution
(1, 'IP12-BLACK-128', 24, '6.1', 'inches'), -- screen size
(1, 'IP12-BLACK-128', 25, '4', 'GB'), -- RAM size
(1, 'IP12-BLACK-128', 26, '2532 x 1170', 'pixels'), -- screen resolution
(1, 'IP12-BLACK-128', 27, '2815', 'mAh'), -- battery capacity
(1, 'IP12-BLACK-128', 28, 'Apple A14 Bionic', NULL), -- processor
(1, 'IP12-BLACK-128', 29, 'Black', NULL), -- color
(1, 'IP12-BLACK-128', 30, 'Lightning', NULL), -- ports
(1, 'IP12-BLACK-128', 31, '2024', NULL), -- year

-- iPhone SE  and variant(blue and red)
(2, 'IPSE-BLUE-64', 22, '64', 'GB'),
(2, 'IPSE-BLUE-64', 23, '12 MP', 'MP'),
(2, 'IPSE-BLUE-64', 24, '4.7', 'inches'),
(2, 'IPSE-BLUE-64', 25, '3', 'GB'),
(2, 'IPSE-BLUE-64', 26, '1334 x 750', 'pixels'),
(2, 'IPSE-BLUE-64', 27, '1821', 'mAh'),
(2, 'IPSE-BLUE-64', 28, 'Apple A13 Bionic', NULL),
(2, 'IPSE-BLUE-64', 29, 'Blue', NULL),
(2, 'IPSE-BLUE-64', 30, 'Lightning', NULL),
(2, 'IPSE-BLUE-64', 31, '2024', NULL), -- year

(2, 'IPSE-RED-64', 22, '64', 'GB'),
(2, 'IPSE-RED-64', 23, '12 MP', 'MP'),
(2, 'IPSE-RED-64', 24, '4.7', 'inches'),
(2, 'IPSE-RED-64', 25, '3', 'GB'),
(2, 'IPSE-RED-64', 26, '1334 x 750', 'pixels'),
(2, 'IPSE-RED-64', 27, '1821', 'mAh'),
(2, 'IPSE-RED-64', 28, 'Apple A13 Bionic', NULL),
(2, 'IPSE-RED-64', 29, 'Red', NULL),
(2, 'IPSE-RED-64', 30, 'Lightning', NULL),
(2, 'IPSE-RED-64', 31, '2024', NULL), -- year

-- Pixel 5
(3, 'PX5', 22, '128', 'GB'),
(3, 'PX5', 23, '12.2 MP', 'MP'),
(3, 'PX5', 24, '6', 'inches'),
(3, 'PX5', 25, '8', 'GB'),
(3, 'PX5', 26, '2340 x 1080', 'pixels'),
(3, 'PX5', 27, '4080', 'mAh'),
(3, 'PX5', 28, 'Qualcomm Snapdragon 765G', NULL),
(3, 'PX5', 29, 'Black', NULL),
(3, 'PX5', 30, 'USB-C', NULL),
(3, 'PX5', 31, '2024', NULL), -- year

-- OnePlus 9 Pro
(4, 'OP9P', 22, '256', 'GB'),
(4, 'OP9P', 23, '48 MP', 'MP'),
(4, 'OP9P', 24, '6.7', 'inches'),
(4, 'OP9P', 25, '12', 'GB'),
(4, 'OP9P', 26, '1440 x 3216', 'pixels'),
(4, 'OP9P', 27, '4500', 'mAh'),
(4, 'OP9P', 28, 'Qualcomm Snapdragon 888', NULL),
(4, 'OP9P', 29, 'Various colors', NULL),
(4, 'OP9P', 30, 'USB-C', NULL),
(4, 'OP9P', 31, '2024', NULL), -- year

-- Galaxy S21
(5, 'GS21', 22, '128', 'GB'), -- storage capacity
(5, 'GS21', 23, '12 MP', 'MP'), -- camera resolution
(5, 'GS21', 24, '6.2', 'inches'), -- screen size
(5, 'GS21', 25, '8', 'GB'), -- RAM size
(5, 'GS21', 26, '2400 x 1080', 'pixels'), -- screen resolution
(5, 'GS21', 27, '4000', 'mAh'), -- battery capacity
(5, 'GS21', 28, 'Samsung Exynos 2100', NULL), -- processor
(5, 'GS21', 29, 'Various colors', NULL), -- color
(5, 'GS21', 30, 'USB-C', NULL), -- ports
(5, 'GS21', 31, '2024', NULL), -- year

-- AirPods Pro
(6, 'APRO1', 74, 'White', NULL), -- color
(6, 'APRO1', 75, 'Wireless', NULL), -- connection type
(6, 'APRO1', 76, 'Lightning', NULL), -- ports
(6, 'APRO1', 77, '2024', NULL), -- year

-- AirPods 2
(7, 'APO2', 74, 'White', NULL),
(7, 'APO2', 75, 'Wireless', NULL),
(7, 'APO2', 76, 'Lightning', NULL),
(7, 'APO2', 77, '2024', NULL), -- year

-- OnePlus Buds Pro
(8, 'OBPRO', 74, 'Black', NULL),
(8, 'OBPRO', 75, 'Wireless', NULL),
(8, 'OBPRO', 76, 'USB-C', NULL),
(8, 'OBPRO', 77, '2024', NULL), -- year

-- ipad pro
(9, 'IPPRO', 54, '256', 'GB'), -- storage capacity
(9, 'IPPRO', 55, '12 MP', 'MP'), -- camera resolution
(9, 'IPPRO', 56, '12.9', 'inches'), -- screen size
(9, 'IPPRO', 57, '16', 'GB'), -- RAM size
(9, 'IPPRO', 58, '2732 x 2048', 'pixels'), -- screen resolution
(9, 'IPPRO', 59, '10090', 'mAh'), -- battery capacity
(9, 'IPPRO', 60, 'Apple M1', NULL), -- processor
(9, 'IPPRO', 61, 'Various colors', NULL), -- color
(9, 'IPPRO', 62, 'USB-C', NULL), -- ports
(9, 'IPPRO', 63, '2024', NULL), -- year

-- OnePlus Watch 3
(10, 'OPW3', 64, '4', 'GB'), -- storage capacity
(10, 'OPW3', 65, NULL, NULL), -- camera resolution (not applicable)
(10, 'OPW3', 66, '1.59', 'inches'), -- screen size
(10, 'OPW3', 67, '1', 'GB'), -- RAM size
(10, 'OPW3', 68, '454 x 454', 'pixels'), -- screen resolution
(10, 'OPW3', 69, '402', 'mAh'), -- battery capacity
(10, 'OPW3', 70, 'Snapdragon Wear 4100', NULL), -- processor
(10, 'OPW3', 71, 'Various colors', NULL), -- color
(10, 'OPW3', 72, 'Charging dock', NULL), -- ports
(10, 'OPW3', 73, '2024', NULL), -- year

-- Galaxy Watch3
(11, 'GW3', 64, '8', 'GB'), -- storage capacity
(11, 'GW3', 65, NULL, NULL), -- camera resolution (not applicable)
(11, 'GW3', 66, '1.4', 'inches'), -- screen size
(11, 'GW3', 67, '1', 'GB'), -- RAM size
(11, 'GW3', 68, '360 x 360', 'pixels'), -- screen resolution
(11, 'GW3', 69, '340', 'mAh'), -- battery capacity
(11, 'GW3', 70, 'Exynos 9110', NULL), -- processor
(11, 'GW3', 71, 'Various colors', NULL), -- color
(11, 'GW3', 72, 'Wireless charging', NULL), -- ports
(11, 'GW3', 73, '2024', NULL), -- year

-- T5 Portable SSD
(12, 'T5SSD', 78, 'SSD', NULL), -- storage type
(12, 'T5SSD', 79, 'USB-C', NULL), -- connection port
(12, 'T5SSD', 80, '1', 'TB'), -- storage size
(12, 'T5SSD', 81, 'Up to 540 MB/s', 'MB/s'), -- storage speed
(12, 'T5SSD', 82, '2024', NULL), -- year

-- MacBook Pro
(13, 'MBP', 40, '512', 'GB'), -- storage capacity
(13, 'MBP', 41, '13.3', 'inches'), -- screen size
(13, 'MBP', 42, '16', 'GB'), -- RAM size
(13, 'MBP', 43, '2560 x 1600', 'pixels'), -- screen resolution
(13, 'MBP', 44, 'Up to 10 hours', 'hours'), -- battery capacity
(13, 'MBP', 45, 'Intel Core i7', NULL), -- processor
(13, 'MBP', 46, 'Silver', NULL), -- color
(13, 'MBP', 47, 'English', NULL), -- keyboard language
(13, 'MBP', 48, 'USB-C', NULL), -- ports
(13, 'MBP', 49, '2024', NULL), -- year

-- XPS 13
(14, 'XPS13', 40, '1', 'TB'),
(14, 'XPS13', 41, '13.4', 'inches'),
(14, 'XPS13', 42, '16', 'GB'),
(14, 'XPS13', 43, '3840 x 2400', 'pixels'),
(14, 'XPS13', 44, 'Up to 14 hours', 'hours'),
(14, 'XPS13', 45, 'Intel Core i7', NULL),
(14, 'XPS13', 46, 'Silver', NULL),
(14, 'XPS13', 47, 'English', NULL),
(14, 'XPS13', 48, 'USB-C', NULL),
(14, 'XPS13', 49, '2024', NULL), -- year

-- ThinkPad X1 Carbon
(15, 'TPX1C', 40, '512', 'GB'),
(15, 'TPX1C', 41, '14', 'inches'),
(15, 'TPX1C', 42, '16', 'GB'),
(15, 'TPX1C', 43, '2560 x 1440', 'pixels'),
(15, 'TPX1C', 44, 'Up to 19.5 hours', 'hours'),
(15, 'TPX1C', 45, 'Intel Core i7', NULL),
(15, 'TPX1C', 46, 'Black', NULL),
(15, 'TPX1C', 47, 'English', NULL),
(15, 'TPX1C', 48, 'HDMI, USB-C', NULL),
(15, 'TPX1C', 49, '2024', NULL), -- year

-- Yoga C940
(16, 'YC940', 40, '1', 'TB'),
(16, 'YC940', 41, '14', 'inches'),
(16, 'YC940', 42, '16', 'GB'),
(16, 'YC940', 43, '3840 x 2160', 'pixels'),
(16, 'YC940', 44, 'Up to 15 hours', 'hours'),
(16, 'YC940', 45, 'Intel Core i7', NULL),
(16, 'YC940', 46, 'Iron Grey', NULL),
(16, 'YC940', 47, 'English', NULL),
(16, 'YC940', 48, 'USB-C', NULL),
(16, 'YC940', 49, '2024', NULL), -- year

-- IdeaPad Gaming 3
(17, 'IPG3', 40, '512', 'GB'),
(17, 'IPG3', 41, '15.6', 'inches'),
(17, 'IPG3', 42, '8', 'GB'),
(17, 'IPG3', 43, '1920 x 1080', 'pixels'),
(17, 'IPG3', 44, 'Up to 9 hours', 'hours'),
(17, 'IPG3', 45, 'AMD Ryzen 5', NULL),
(17, 'IPG3', 46, 'Black', NULL),
(17, 'IPG3', 47, 'English', NULL),
(17, 'IPG3', 48, 'HDMI, USB-C', NULL),
(17, 'IPG3', 49, '2024', NULL), -- year

-- Alienware m15 R5
(18, 'AM15R5', 40, '1', 'TB'),
(18, 'AM15R5', 41, '15.6', 'inches'),
(18, 'AM15R5', 42, '16', 'GB'),
(18, 'AM15R5', 43, '3840 x 2160', 'pixels'),
(18, 'AM15R5', 44, 'Up to 6 hours', 'hours'),
(18, 'AM15R5', 45, 'AMD Ryzen 9', NULL),
(18, 'AM15R5', 46, 'Lunar Light', NULL),
(18, 'AM15R5', 47, 'English', NULL),
(18, 'AM15R5', 48, 'USB-C', NULL),
(18, 'AM15R5', 49, '2024', NULL), -- year

-- Xbox Series X
(19, 'XSX', 50, 'Action, Horror, FPS, RPG', NULL), -- Game type
(19, 'XSX', 51, 'Console', NULL), -- Console type
(19, 'XSX', 52, 'HDMI, USB-A, USB-C, Ethernet', NULL), -- Ports
(19, 'XSX', 53, '2024', NULL), -- year

-- Dell UltraSharp U2720Q
(20, 'DUU2720Q', 83, '27', 'inches'), -- Screen size
(20, 'DUU2720Q', 84, '3840 x 2160', 'pixels'), -- Screen resolution
(20, 'DUU2720Q', 85, '60 Hz', 'Hz'), -- Screen refresh rate
(20, 'DUU2720Q', 86, '16.7 million colors', NULL), -- Color
(20, 'DUU2720Q', 87, 'No', NULL), -- Speaker
(20, 'DUU2720Q', 88, 'HDMI, DisplayPort, USB-C', NULL), -- Ports
(20, 'DUU2720Q', 89, '2024', NULL), -- year

-- Nike Air Max 270
(21, 'NAM270-105', 40, '10', NULL), -- Size
(21, 'NAM270-105', 37, 'Black/White', NULL), -- Color
(21, 'NAM270-105', 41, 'Athletic', NULL), -- Style

(21, 'NAM270-90', 40, '10', NULL), -- Size
(21, 'NAM270-90', 37, 'Black/White', NULL), -- Color
(21, 'NAM270-90', 41, 'Athletic', NULL), -- Style

(21, 'NAM270-75', 40, '10', NULL), -- Size
(21, 'NAM270-75', 37, 'Black/White', NULL), -- Color
(21, 'NAM270-75', 41, 'Athletic', NULL), -- Style

-- Nike ZoomX Vaporfly NEXT
(22, 'NZVNEXT', 4, '9.5', NULL),
(22, 'NZVNEXT', 5, 'Pink/Black', NULL),
(22, 'NZVNEXT', 6, 'Running', NULL),

-- Adidas Ultra Boost
(23, 'AUB', 4, '11', NULL),
(23, 'AUB', 5, 'White', NULL),
(23, 'AUB', 6, 'Athletic', NULL),

-- Adidas Adilette Slides
(24, 'AAS', 7, '8', NULL),
(24, 'AAS', 8, 'Blue', NULL),
(24, 'AAS', 9, 'Slides', NULL),

-- Nike Dri-FIT T-Shirt
(25, 'NDFTS', 1, 'M', NULL),
(25, 'NDFTS', 2, 'Red', NULL),
(25, 'NDFTS', 3, 'Short Sleeve', NULL),

-- Calvin Klein Logo T-Shirt
(26, 'CKLTS-L', 1, 'L', NULL),
(26, 'CKLTS-L', 2, 'Black', NULL),
(26, 'CKLTS-L', 3, 'Short Sleeve', NULL),

(26, 'CKLTS-M', 1, 'M', NULL),
(26, 'CKLTS-M', 2, 'Black', NULL),
(26, 'CKLTS-M', 3, 'Short Sleeve', NULL),

(26, 'CKLTS-S', 1, 'S', NULL),
(26, 'CKLTS-S', 2, 'Black', NULL),
(26, 'CKLTS-S', 3, 'Short Sleeve', NULL),

-- Adidas Essential Track Pants
(27, 'AEPTP', 1, 'XL', NULL),
(27, 'AEPTP', 2, 'Gray', NULL),
(27, 'AEPTP', 3, 'Track Pants', NULL),

-- The Great Gatsby
(28, 'TGG', 17, '9780743273565', NULL), -- ISBN
(28, 'TGG', 18, '180', 'pages'), -- Length
(28, 'TGG', 19, 'English', NULL), -- Language

-- To Kill a Mockingbird
(29, 'TKAM', 17, '9780060935467', NULL),
(29, 'TKAM', 18, '336', 'pages'),
(29, 'TKAM', 19, 'English', NULL),

-- Harry Potter and the Sorcerer's Stone
(30, 'HPSS', 17, '9780590353427', NULL),
(30, 'HPSS', 18, '320', 'pages'),
(30, 'HPSS', 19, 'English', NULL);


DROP TABLE IF EXISTS product_album;
CREATE TABLE product_album (
    id SERIAL PRIMARY KEY,
    name TEXT,
    product_id INTEGER NOT NULL,
    cover_pic TEXT,
    pic_count INTEGER,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO product_album (name, product_id, cover_pic, description)
VALUES
('product album', 1, 'https://i.imgur.com/hFS4omM.png', 'album description'),
('description album', 1, 'https://i.imgur.com/tCUq2GJ.jpeg', 'album description'),
('product album', 2, 'https://i.imgur.com/kZPEccB.jpeg', 'album description'),
('description album', 2, 'https://i.imgur.com/ZBk3W8l.jpeg', 'album description'),
('product album', 3, 'https://i.imgur.com/XULiMJ5.jpeg', 'album description'),
('description album', 3, 'https://i.imgur.com/e5LvUqr.jpeg', 'album description'),
('product album', 4, 'https://i.imgur.com/T7ghiT8.jpeg', 'album description'),
('description album', 4, 'https://i.imgur.com/aiw2EfU.jpeg', 'album description'),
('product album', 5, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('description album', 5, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('product pic', 6, 'https://i.imgur.com/hFS4omM.png', 'album description'),
('description pic', 6, 'https://i.imgur.com/tCUq2GJ.jpeg', 'album description'),
('product pic', 7, 'https://i.imgur.com/kZPEccB.jpeg', 'album description'),
('description pic', 7, 'https://i.imgur.com/ZBk3W8l.jpeg', 'album description'),
('product pic', 8, 'https://i.imgur.com/XULiMJ5.jpeg', 'album description'),
('description pic', 8, 'https://i.imgur.com/e5LvUqr.jpeg', 'album description'),
('product pic', 9, 'https://i.imgur.com/T7ghiT8.jpeg', 'album description'),
('description pic', 9, 'https://i.imgur.com/aiw2EfU.jpeg', 'album description'),
('product pic', 10, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('description pic', 10, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('product album', 11, 'https://i.imgur.com/hFS4omM.png', 'album description'),
('description album', 11, 'https://i.imgur.com/tCUq2GJ.jpeg', 'album description'),
('product album', 12, 'https://i.imgur.com/kZPEccB.jpeg', 'album description'),
('description album', 12, 'https://i.imgur.com/ZBk3W8l.jpeg', 'album description'),
('product album', 13, 'https://i.imgur.com/XULiMJ5.jpeg', 'album description'),
('description album', 13, 'https://i.imgur.com/e5LvUqr.jpeg', 'album description'),
('product album', 14, 'https://i.imgur.com/T7ghiT8.jpeg', 'album description'),
('description album', 14, 'https://i.imgur.com/aiw2EfU.jpeg', 'album description'),
('product album', 15, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('description album', 15, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('product pic', 16, 'https://i.imgur.com/hFS4omM.png', 'album description'),
('description pic', 16, 'https://i.imgur.com/tCUq2GJ.jpeg', 'album description'),
('product pic', 17, 'https://i.imgur.com/kZPEccB.jpeg', 'album description'),
('description pic', 17, 'https://i.imgur.com/ZBk3W8l.jpeg', 'album description'),
('product pic', 18, 'https://i.imgur.com/XULiMJ5.jpeg', 'album description'),
('description pic', 18, 'https://i.imgur.com/e5LvUqr.jpeg', 'album description'),
('product pic', 19, 'https://i.imgur.com/T7ghiT8.jpeg', 'album description'),
('description pic', 19, 'https://i.imgur.com/aiw2EfU.jpeg', 'album description'),
('product pic', 20, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('description pic', 20, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('product album', 21, 'https://i.imgur.com/hFS4omM.png', 'album description'),
('description album', 21, 'https://i.imgur.com/tCUq2GJ.jpeg', 'album description'),
('product album', 22, 'https://i.imgur.com/kZPEccB.jpeg', 'album description'),
('description album', 22, 'https://i.imgur.com/ZBk3W8l.jpeg', 'album description'),
('product album', 23, 'https://i.imgur.com/XULiMJ5.jpeg', 'album description'),
('description album', 23, 'https://i.imgur.com/e5LvUqr.jpeg', 'album description'),
('product album', 24, 'https://i.imgur.com/T7ghiT8.jpeg', 'album description'),
('description album', 24, 'https://i.imgur.com/aiw2EfU.jpeg', 'album description'),
('product album', 25, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('description album', 25, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('product pic', 26, 'https://i.imgur.com/hFS4omM.png', 'album description'),
('description pic', 26, 'https://i.imgur.com/tCUq2GJ.jpeg', 'album description'),
('product pic', 27, 'https://i.imgur.com/kZPEccB.jpeg', 'album description'),
('description pic', 27, 'https://i.imgur.com/ZBk3W8l.jpeg', 'album description'),
('product pic', 28, 'https://i.imgur.com/XULiMJ5.jpeg', 'album description'),
('description pic', 28, 'https://i.imgur.com/e5LvUqr.jpeg', 'album description'),
('product pic', 29, 'https://i.imgur.com/T7ghiT8.jpeg', 'album description'),
('description pic', 29, 'https://i.imgur.com/aiw2EfU.jpeg', 'album description'),
('product pic', 30, 'https://i.imgur.com/AaevbdO.png', 'album description'),
('description pic', 30, 'https://i.imgur.com/AaevbdO.png', 'album description');


DROP TABLE IF EXISTS product_album_picture;
CREATE TABLE product_album_picture (
    id SERIAL PRIMARY KEY,
    product_album_id INTEGER NOT NULL,
    filename VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO product_album_picture (product_album_id, filename)
VALUES
(1, 'https://i.imgur.com/hFS4omM.png'),
(1, 'https://i.imgur.com/1CwQZDS.jpeg'),
(2, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(3, 'https://i.imgur.com/kZPEccB.jpeg'),
(3, 'https://i.imgur.com/ZBk3W8l.jpeg'),
(4, 'https://i.imgur.com/XULiMJ5.jpeg'),
(5, 'https://i.imgur.com/e5LvUqr.jpeg'),
(5, 'https://i.imgur.com/T7ghiT8.jpeg'),
(6, 'https://i.imgur.com/aiw2EfU.jpeg'),
(7, 'https://i.imgur.com/AaevbdO.png'),
(7, 'https://i.imgur.com/hFS4omM.png'),
(8, 'https://i.imgur.com/1CwQZDS.jpeg'),
(9, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(9, 'https://i.imgur.com/kZPEccB.jpeg'),
(10, 'https://i.imgur.com/ZBk3W8l.jpeg'),
(11, 'https://i.imgur.com/XULiMJ5.jpeg'),
(11, 'https://i.imgur.com/e5LvUqr.jpeg'),
(12, 'https://i.imgur.com/T7ghiT8.jpeg'),
(13, 'https://i.imgur.com/aiw2EfU.jpeg'),
(13, 'https://i.imgur.com/AaevbdO.png'),
(14, 'https://i.imgur.com/hFS4omM.png'),
(15, 'https://i.imgur.com/1CwQZDS.jpeg'),
(15, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(16, 'https://i.imgur.com/kZPEccB.jpeg'),
(17, 'https://i.imgur.com/ZBk3W8l.jpeg'),
(17, 'https://i.imgur.com/XULiMJ5.jpeg'),
(18, 'https://i.imgur.com/e5LvUqr.jpeg'),
(19, 'https://i.imgur.com/T7ghiT8.jpeg'),
(19, 'https://i.imgur.com/aiw2EfU.jpeg'),
(20, 'https://i.imgur.com/AaevbdO.png'),
(21, 'https://i.imgur.com/hFS4omM.png'),
(21, 'https://i.imgur.com/1CwQZDS.jpeg'),
(22, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(23, 'https://i.imgur.com/kZPEccB.jpeg'),
(23, 'https://i.imgur.com/ZBk3W8l.jpeg'),
(24, 'https://i.imgur.com/XULiMJ5.jpeg'),
(25, 'https://i.imgur.com/e5LvUqr.jpeg'),
(25, 'https://i.imgur.com/T7ghiT8.jpeg'),
(26, 'https://i.imgur.com/aiw2EfU.jpeg'),
(27, 'https://i.imgur.com/AaevbdO.png'),
(27, 'https://i.imgur.com/hFS4omM.png'),
(28, 'https://i.imgur.com/1CwQZDS.jpeg'),
(29, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(29, 'https://i.imgur.com/kZPEccB.jpeg'),
(30, 'https://i.imgur.com/ZBk3W8l.jpeg'),
(31, 'https://i.imgur.com/hFS4omM.png'),
(31, 'https://i.imgur.com/1CwQZDS.jpeg'),
(32, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(33, 'https://i.imgur.com/kZPEccB.jpeg'),
(33, 'https://i.imgur.com/ZBk3W8l.jpeg'),
(34, 'https://i.imgur.com/XULiMJ5.jpeg'),
(35, 'https://i.imgur.com/e5LvUqr.jpeg'),
(35, 'https://i.imgur.com/T7ghiT8.jpeg'),
(36, 'https://i.imgur.com/aiw2EfU.jpeg'),
(37, 'https://i.imgur.com/AaevbdO.png'),
(37, 'https://i.imgur.com/hFS4omM.png'),
(38, 'https://i.imgur.com/1CwQZDS.jpeg'),
(39, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(39, 'https://i.imgur.com/kZPEccB.jpeg'),
(40, 'https://i.imgur.com/ZBk3W8l.jpeg'),
(41, 'https://i.imgur.com/hFS4omM.png'),
(41, 'https://i.imgur.com/1CwQZDS.jpeg'),
(42, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(43, 'https://i.imgur.com/kZPEccB.jpeg'),
(43, 'https://i.imgur.com/ZBk3W8l.jpeg'),
(44, 'https://i.imgur.com/XULiMJ5.jpeg'),
(45, 'https://i.imgur.com/e5LvUqr.jpeg'),
(45, 'https://i.imgur.com/T7ghiT8.jpeg'),
(46, 'https://i.imgur.com/aiw2EfU.jpeg'),
(47, 'https://i.imgur.com/AaevbdO.png'),
(47, 'https://i.imgur.com/hFS4omM.png'),
(48, 'https://i.imgur.com/1CwQZDS.jpeg'),
(49, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(49, 'https://i.imgur.com/kZPEccB.jpeg'),
(50, 'https://i.imgur.com/ZBk3W8l.jpeg'),
(51, 'https://i.imgur.com/hFS4omM.png'),
(51, 'https://i.imgur.com/1CwQZDS.jpeg'),
(52, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(53, 'https://i.imgur.com/kZPEccB.jpeg'),
(53, 'https://i.imgur.com/ZBk3W8l.jpeg'),
(54, 'https://i.imgur.com/XULiMJ5.jpeg'),
(55, 'https://i.imgur.com/e5LvUqr.jpeg'),
(55, 'https://i.imgur.com/T7ghiT8.jpeg'),
(56, 'https://i.imgur.com/aiw2EfU.jpeg'),
(57, 'https://i.imgur.com/AaevbdO.png'),
(57, 'https://i.imgur.com/hFS4omM.png'),
(58, 'https://i.imgur.com/1CwQZDS.jpeg'),
(59, 'https://i.imgur.com/tCUq2GJ.jpeg'),
(59, 'https://i.imgur.com/kZPEccB.jpeg'),
(60, 'https://i.imgur.com/ZBk3W8l.jpeg');


-- product get two album, one for preview and the other is in description
DROP TABLE IF EXISTS product_update_log;
CREATE TABLE product_update_log (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    price_old DECIMAL(10, 2) NOT NULL,
    price_new DECIMAL(10, 2) NOT NULL,
    sale_price_old DECIMAL(10, 2) NOT NULL,
    sale_price_new DECIMAL(10, 2) NOT NULL,
    old_stock INTEGER,
    added_stock INTEGER,
    total_stock INTEGER,
    update_action TEXT NOT NULL,
    operator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO product_update_log (product_id, price_old, price_new, sale_price_old, sale_price_new, old_stock, added_stock, total_stock, update_action, operator)
VALUES
(1, 899.99, 899.99, 899.99, 899.99, 100, 0, 100, 'UPDATE', 'jun'),
(2, 499.99, 499.99, 499.99, 499.99, 50, 0, 50, 'UPDATE', 'jun'),
(3, 249.99, 249.99, 249.99, 249.99, 200, 0, 200, 'UPDATE', 'jun'),
(4, 1099.99, 1099.99, 1099.99, 1099.99, 150, 0, 150, 'UPDATE', 'jun'),
(5, 349.99, 349.99, 349.99, 349.99, 100, 0, 100, 'UPDATE', 'jun'),
(6, 179.99, 179.99, 179.99, 179.99, 250, 0, 250, 'UPDATE', 'jun');


DROP TABLE IF EXISTS review;
CREATE TABLE review (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    sku_code TEXT,
    member_id UUID NOT NULL,
    member_name TEXT,
    member_icon TEXT,
    star INTEGER,
    tittle TEXT,
    likes DECIMAL(10, 1) DEFAULT 1,
    verify_status verification_status_enum DEFAULT 'not_verified',
    lifecycle_status lifecycle_status_enum DEFAULT 'normal',
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE review
ADD CONSTRAINT unique_product_member_review UNIQUE (product_id, member_id);

INSERT INTO review (product_id, sku_code, member_id, member_name, member_icon, star, tittle, likes, content)
VALUES
(1, 'IP12-RED-128', 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 'user1', 'icon.jpg', 3.5, 'size smaller than expected', 1, 'size was smaller than expected'),
(1, 'IP12-WHITE-128', 'bb6604eb-080e-4595-9ae8-32c55cbbb35b', 'user1', 'icon.jpg', 1, 'item arrive late', 1, 'late'),
(1, 'IP12-BLACK-128', '3bbc1316-f71a-4475-9abe-ccf281acdc0b', 'user3', 'icon.jpg', 5, 'good',1, 'item is good'),
(3, 'PX5', 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 'user1', 'icon.jpg', 1, 'good',1, 'item is good'),
(3, 'PX5', 'bb6604eb-080e-4595-9ae8-32c55cbbb35b', 'user2', 'icon.jpg', 5, 'good',1, 'item is good'),
(14, 'XPS13', 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 'user1', 'icon.jpg', 1, 'good',1, 'item is good'),
(14, 'XPS13', 'bb6604eb-080e-4595-9ae8-32c55cbbb35b', 'user2', 'icon.jpg', 5, 'good',1, 'item is good'),
(15, 'TPX1C', 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 'user1', 'icon.jpg', 5, 'good',1, 'item is good'),
(15, 'TPX1C', 'bb6604eb-080e-4595-9ae8-32c55cbbb35b', 'user2', 'icon.jpg', 1, 'good',1, 'item is good'),
(15, 'TPX1C', '3bbc1316-f71a-4475-9abe-ccf281acdc0b', 'user3', 'icon.jpg', 1, 'good',1, 'item is good');


DROP TABLE IF EXISTS review_album;
CREATE TABLE review_album (
    id SERIAL PRIMARY KEY,
    review_id INTEGER NOT NULL,
    pic_count INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO review_album (review_id, pic_count)
VALUES
(1, 3),
(2, 1),
(3, 2),
(4, 2),
(5, 1),
(6, 1),
(7, 1),
(8, 1),
(9, 1),
(10, 2);


DROP TABLE IF EXISTS review_album_picture;
CREATE TABLE review_album_picture (
    id SERIAL PRIMARY KEY,
    review_album_id INTEGER NOT NULL,
    filename VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO review_album_picture (review_album_id, filename)
VALUES
(1, 'https://i.imgur.com/rzocQcD.jpeg'),
(1, 'https://i.imgur.com/LUszOiA.jpeg'),
(1, 'https://i.imgur.com/IQAzB6P.jpeg'),
(2, 'https://i.imgur.com/IQAzB6P.jpeg'),
(3, 'https://i.imgur.com/Kz8xPFg.png'),
(3, 'https://i.imgur.com/Kz8xPFg.png'),
(4, 'https://i.imgur.com/detILKk.png'),
(4, 'https://i.imgur.com/detILKk.png'),
(5, 'https://i.imgur.com/rzocQcD.jpeg'),
(6, 'https://i.imgur.com/LUszOiA.jpeg'),
(7, 'https://i.imgur.com/IQAzB6P.jpeg'),
(8, 'https://i.imgur.com/Kz8xPFg.png'),
(9, 'https://i.imgur.com/detILKk.png'),
(10, 'https://i.imgur.com/rzocQcD.jpeg'),
(10, 'https://i.imgur.com/LUszOiA.jpeg');


DROP TABLE IF EXISTS review_update_log;
CREATE TABLE review_update_log (
    id SERIAL PRIMARY KEY,
    review_id INTEGER NOT NULL,
    update_action TEXT NOT NULL,
    operator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO review_update_log (review_id, update_action, operator)
VALUES
(1, 'created', 'David'),
(2, 'updated', 'Sarah'),
(3, 'created', 'Alice'),
(4, 'deleted', 'Chris'),
(5, 'created', 'John'),
(6, 'updated', 'Mike'),
(7, 'created', 'Bob'),
(8, 'updated', 'Linda'),
(9, 'created', 'Sophia'),
(10, 'deleted', 'Emily');


-------------------------------
---------- UMS ----------------
-------------------------------
-- user related management system ---

-- Enable the pgcrypto extension to use gen_random_uuid() for UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

DROP TABLE IF EXISTS member CASCADE;
CREATE TABLE member (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),  -- Use UUID as primary key with auto generation
    username TEXT UNIQUE ,
    password TEXT,
    name TEXT,
    phone_number TEXT,
    email TEXT UNIQUE NOT NULL,
    email_subscription INTEGER DEFAULT 1,
    status account_status_enum DEFAULT 'active',
    verified_status verification_status_enum DEFAULT 'not_verified',
    lifecycle_status lifecycle_status_enum DEFAULT 'normal',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    source_type source_type_enum DEFAULT 'web'   -- where user first created. web user , mobile user
);
---------------User  all password is password
INSERT INTO member (id, username, password, name, phone_number, email, verified_status)
VALUES
('a57f32ad-8ebc-486e-940d-7abb2ece682f', 'user1', '$2a$10$PHcLPlJod/fKyjMUsGuSVeVnI0.EKudDleRT9vM9jqCJzL9QvC5Ju', 'Jun', '212-212-2222', 'Jun@gmail.com', 'verified'),
('bb6604eb-080e-4595-9ae8-32c55cbbb35b', 'user2', '$2a$10$pSHd2ngUssBZYRlHQQaKu.rb0me5ZAgld0fVASB50vrMslLb8md0a', 'John', '877-393-4448', 'John@gmail.com', 'verified'),
('3bbc1316-f71a-4475-9abe-ccf281acdc0b', 'user3', '$2a$10$xEbGJ1QHr/CZ.ltRIP4A9.K27Sq3HJ4Dh/sN0ssd5GwkaPbjPRW9S', 'Jane', '112-323-1111', 'Jane@gmail.com', 'verified');

DROP TABLE IF EXISTS member_icon;
CREATE TABLE member_icon (
    id SERIAL PRIMARY KEY,
    member_id UUID NOT NULL,
    filename TEXT,
    CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES member (id)
);

INSERT INTO member_icon (member_id, filename)
VALUES
('a57f32ad-8ebc-486e-940d-7abb2ece682f', 'https://i.imgur.com/aPrCAdn.png'),
('bb6604eb-080e-4595-9ae8-32c55cbbb35b', 'https://i.imgur.com/1URlVYg.png'),
('3bbc1316-f71a-4475-9abe-ccf281acdc0b', 'https://i.imgur.com/IG2yW8k.jpeg');

DROP TABLE IF EXISTS address;
CREATE TABLE address (
    id SERIAL PRIMARY KEY,
    member_id UUID NOT NULL,
    receiver_name TEXT,
    phone_number TEXT,
    detail_address TEXT,
    city TEXT,
    state TEXT,
    zip_code TEXT,
    note TEXT,
    CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES member (id)
);

INSERT INTO address (member_id, receiver_name, phone_number, detail_address, city, state, zip_code, note)
VALUES
('a57f32ad-8ebc-486e-940d-7abb2ece682f', 'Jun',  '212-212-2222', '1 1st street 2nd ave', 'Chicago', 'Illinois', '60007', ''),
('bb6604eb-080e-4595-9ae8-32c55cbbb35b', 'John', '111-111-1111', '2 2nd street 3rd ave Apt 4F', 'Dallas', 'Texas', '75001', 'please call, door bell broken'),
('3bbc1316-f71a-4475-9abe-ccf281acdc0b', 'Jane', '212-212-2222', '3 4st street 5nd ave', 'San Francisco', 'California', '94016', '');

DROP TABLE IF EXISTS member_login_log;
CREATE TABLE member_login_log (
    id SERIAL PRIMARY KEY,
    member_id UUID NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address TEXT,
    login_type TEXT, -- 0/1/2 pc TEXT, ios TEXT, android
    CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES member (id)
);

--- login type,pc/android/IOS   = 0/1/2
INSERT INTO member_login_log (member_id, ip_address, login_type)
VALUES
('a57f32ad-8ebc-486e-940d-7abb2ece682f', '127.0.0.1', '0'),
('a57f32ad-8ebc-486e-940d-7abb2ece682f', '127.0.0.1', '0'),
('bb6604eb-080e-4595-9ae8-32c55cbbb35b', '127.0.0.1', '1'),
('bb6604eb-080e-4595-9ae8-32c55cbbb35b', '127.0.0.1', '1'),
('3bbc1316-f71a-4475-9abe-ccf281acdc0b', '127.0.0.1', '0'),
('3bbc1316-f71a-4475-9abe-ccf281acdc0b', '127.0.0.1', '2');


DROP TABLE IF EXISTS member_change_log;
CREATE TABLE member_change_log (
    id SERIAL PRIMARY KEY,
    member_id UUID NOT NULL,
    update_action VARCHAR(255) NOT NULL,
    operator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES member (id)
);

INSERT INTO member_change_log (member_id, update_action, operator)
VALUES
('a57f32ad-8ebc-486e-940d-7abb2ece682f', 'Update', 'Jun'),

('a57f32ad-8ebc-486e-940d-7abb2ece682f', 'Create', 'Jun'),
('a57f32ad-8ebc-486e-940d-7abb2ece682f', 'Update', 'Jun'),
('a57f32ad-8ebc-486e-940d-7abb2ece682f', 'Delete', 'Jun'),

('bb6604eb-080e-4595-9ae8-32c55cbbb35b', 'Create', 'John'),
('bb6604eb-080e-4595-9ae8-32c55cbbb35b', 'Update', 'John'),
('bb6604eb-080e-4595-9ae8-32c55cbbb35b', 'Delete', 'John'),

('3bbc1316-f71a-4475-9abe-ccf281acdc0b', 'Create', 'Jane'),
('3bbc1316-f71a-4475-9abe-ccf281acdc0b', 'Update', 'Jane'),
('3bbc1316-f71a-4475-9abe-ccf281acdc0b', 'Create', 'Jane'),

('a57f32ad-8ebc-486e-940d-7abb2ece682f', 'Update', 'Admin - Jun');


---------Admin related----------------
DROP TABLE IF EXISTS admin CASCADE;
CREATE TABLE admin (
    id SERIAL PRIMARY KEY,
    username TEXT UNIQUE,
    password TEXT,
    icon TEXT,
    email TEXT UNIQUE NOT NULL,
    name TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    status account_status_enum DEFAULT 'inactive'
);

DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name TEXT,
    description TEXT,
    status account_status_enum DEFAULT 'inactive'
);

DROP TABLE IF EXISTS permission;
CREATE TABLE permission (
    id SERIAL PRIMARY KEY,
    name TEXT,
    permission_key TEXT NOT NULL,
    status account_status_enum DEFAULT 'inactive'
);

DROP TABLE IF EXISTS role_permission_relation;
CREATE TABLE role_permission_relation (
    id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL,
    permission_id INTEGER NOT NULL
);

DROP TABLE IF EXISTS admin_role_relation;
CREATE TABLE admin_role_relation (
    id SERIAL PRIMARY KEY,
    admin_id INTEGER NOT NULL,
    role_id INTEGER,
    assigned_by INTEGER, -- the admin who granted the role
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status account_status_enum DEFAULT 'inactive'
);

DROP TABLE IF EXISTS admin_permission_relation;
CREATE TABLE admin_permission_relation ( -- TODO: might add a permission and/or role change log too
    id SERIAL PRIMARY KEY,
    admin_id INTEGER NOT NULL,
    permission_id INTEGER NOT NULL,
    assigned_by INTEGER, -- the admin who granted the permission
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status account_status_enum DEFAULT 'inactive'    -- root admin can revoke or enable the permission
);

DROP TABLE IF EXISTS admin_login_log;
CREATE TABLE admin_login_log (
    id SERIAL PRIMARY KEY,
    admin_id INTEGER NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(20),
    user_agent INTEGER
);


--------------Admin
-- username : adminacct  password: adminpass      first admin have all permission, second is for order and only have order permission
-- and third admin is for user and have all user permission.
-- username : devacct   password: devpass
INSERT INTO admin(username, password, email, name, status)
VALUES
('adminacct', '$2a$10$c.FVHJ7x9Gedv.StYqdOB.FB1dNVCLBxS76ZbLutbTHwL15hcFGh2', 'admin@gmail.com', 'jun', 'active'),
('adminacctorder', '$2a$10$c.FVHJ7x9Gedv.StYqdOB.FB1dNVCLBxS76ZbLutbTHwL15hcFGh2', 'order@gmail.com', 'jun', 'active'),
('adminacctuser', '$2a$10$c.FVHJ7x9Gedv.StYqdOB.FB1dNVCLBxS76ZbLutbTHwL15hcFGh2', 'user@gmail.com', 'jun', 'active'),
('devacct', '$2a$10$zykJppm18avEb79CGEtFjOIwKlgUJ4BeMFiF8HGjccVMgJ8XTjZpy', 'dev@gmail.com', 'dev', 'active');

INSERT INTO roles (name, description, status)
VALUES
('ROLE_admin_content', 'manage content issues', 'active'),
('ROLE_admin_order', 'manage order issues', 'active'),
('ROLE_admin_product', 'manage product issues', 'active'),
('ROLE_admin_sale', 'manage sale issues', 'active'),
('ROLE_admin_user', 'manage user related issue', 'active'),
('ROLE_admin_root', 'root', 'active');


INSERT INTO permission (name, permission_key, status)
VALUES
-- CRUD Content
('create content', 'content:create', 'active'),
('read content', 'content:read', 'active'),
('update content', 'content:update', 'active'),
('delete content', 'content:delete', 'active'),

-- CRUD Order
('create order', 'order:create', 'active'),
('read order', 'order:read', 'active'),
('update order', 'order:update', 'active'),
('delete order', 'order:delete', 'active'),

('create order return', 'order:return:create', 'active'),
('read order return', 'order:return:read', 'active'),
('update order return', 'order:return:update', 'active'),
('delete order return', 'order:return:delete', 'active'),

-- CRUD Product
('create product', 'product:create', 'active'),
('read product', 'product:read', 'active'),
('update product', 'product:update', 'active'),
('delete product', 'product:delete', 'active'),

('create brand', 'brand:create', 'active'),
('read brand', 'brand:read', 'active'),
('update brand', 'brand:update', 'active'),
('delete brand', 'brand:delete', 'active'),

('create review', 'review:create', 'active'),
('read review', 'review:read', 'active'),
('update review', 'review:update', 'active'),
('delete review', 'review:delete', 'active'),

-- CRUD Sales
('create sales', 'sales:create', 'active'),
('read sales', 'sales:read', 'active'),
('update sales', 'sales:update', 'active'),
('delete sales', 'sales:delete', 'active'),

('create coupon', 'coupon:create', 'active'),
('read coupon', 'coupon:read', 'active'),
('update coupon', 'coupon:update', 'active'),
('delete coupon', 'coupon:delete', 'active'),

-- CRUD User
('create user', 'user:create', 'active'),
('read user', 'user:read', 'active'),
('update user', 'user:update', 'active'),
('delete user', 'user:delete', 'active'),

('create admin', 'admin:create', 'active'),
('read admin', 'admin:read', 'active'),
('update admin', 'admin:update', 'active'),
('delete admin', 'admin:delete', 'active');


-- role to permission
-- roles have different permission
INSERT INTO role_permission_relation(role_id, permission_id)
VALUES
('1', '1'), -- ROLE_admin_content
('1', '2'),
('1', '3'),
('1', '4'),

('2', '5'), -- ROLE_admin_order
('2', '6'),
('2', '7'),
('2', '8'),

('2', '9'),
('2', '10'),
('2', '11'),
('2', '12'),

('3', '13'), -- ROLE_admin_product
('3', '14'),
('3', '15'),
('3', '16'),

('3', '17'),
('3', '18'),
('3', '19'),
('3', '20'),

('3', '21'),
('3', '22'),
('3', '23'),
('3', '24'),

('4', '25'), -- ROLE_admin_sales
('4', '26'),
('4', '27'),
('4', '28'),

('4', '29'),
('4', '30'),
('4', '31'),
('4', '32'),

('5', '33'), -- ROLE_admin_user
('5', '34'),
('5', '35'),
('5', '36'),

('5', '37'),
('5', '38'),
('5', '39'),
('5', '40'),

('7', '1'), -- ROLE_member, content
('7', '2'),
('7', '3'),
('7', '4'),

('7', '5'), -- ROLE_member, order and order return
('7', '6'),
('7', '7'),
('7', '8'),

('7', '9'),
('7', '10'),
('7', '11'),
('7', '12'),

('7', '21'), -- ROLE_member, product review
('7', '22'),
('7', '23'),
('7', '24'),

('7', '33'), -- ROLE_member, user
('7', '34'),
('7', '35'),
('7', '36');


-- admin have many different roles
-- different roles have different permission
-- main admin have all permission/roles, have a root admin as role
INSERT INTO admin_role_relation(admin_id, role_id, assigned_by, status)
VALUES
(1, 1, 1, 'active'), -- root admin with all the roles
(1, 2, 1, 'active'),
(1, 3, 1, 'active'),
(1, 4, 1, 'active'),
(1, 5, 1, 'active'),
(1, 6, 1, 'active'),

(2, 2, 1, 'active'), -- order admin responsible for order management

(3, 5, 1, 'active'); -- user admin responsible for user management


-- TODO: RoleHierarchy roleHierarchy bean, simply root admin
INSERT INTO admin_permission_relation(admin_id, permission_id, assigned_by, status)
VALUES
(1, 1, 1, 'active'), -- root admin with all the permissions
(1, 2, 1, 'active'),
(1, 3, 1, 'active'),
(1, 4, 1, 'active'),
(1, 5, 1, 'active'),
(1, 6, 1, 'active'),
(1, 7, 1, 'active'),
(1, 8, 1, 'active'),
(1, 9, 1, 'active'),
(1, 10, 1, 'active'),
(1, 11, 1, 'active'),
(1, 12, 1, 'active'),
(1, 13, 1, 'active'),
(1, 14, 1, 'active'),
(1, 15, 1, 'active'),
(1, 16, 1, 'active'),
(1, 17, 1, 'active'),
(1, 18, 1, 'active'),
(1, 19, 1, 'active'),
(1, 20, 1, 'active'),
(1, 21, 1, 'active'),
(1, 22, 1, 'active'),
(1, 23, 1, 'active'),
(1, 24, 1, 'active'),
(1, 25, 1, 'active'),
(1, 26, 1, 'active'),
(1, 27, 1, 'active'),
(1, 28, 1, 'active'),
(1, 29, 1, 'active'),
(1, 30, 1, 'active'),
(1, 31, 1, 'active'),
(1, 32, 1, 'active'),
(1, 33, 1, 'active'),
(1, 34, 1, 'active'),
(1, 35, 1, 'active'),
(1, 36, 1, 'active'),
(1, 37, 1, 'active'),
(1, 38, 1, 'active'),
(1, 39, 1, 'active'),
(1, 40, 1, 'active'),

(2, 5, 1, 'active'), -- order admin responsible for order management
(2, 6, 1, 'active'),
(2, 7, 1, 'active'),
(2, 8, 1, 'active'),

(2, 9, 1, 'active'),
(2, 10, 1, 'active'),
(2, 11, 1, 'active'),
(2, 12, 1, 'active'),

(3, 33, 1, 'active'), -- user admin responsible for user management
(3, 34, 1, 'active'),
(3, 35, 1, 'active'),
(3, 36, 1, 'active'),

(3, 37, 1, 'active'),
(3, 38, 1, 'active'),
(3, 39, 1, 'active'),
(3, 40, 1, 'active');

-- user_agent 1 -> pc, 2 -> mobile users
insert into admin_login_log (admin_id, ip_address, user_agent)
VALUES
(2, '134.163.118.46', 2),
(1, '63.34.62.42', 2),
(1, '135.233.10.69', 1),
(1, '20.247.202.109', 2),
(2, '36.98.223.124', 2),
(3, '173.205.10.223', 1),
(3, '196.204.187.66', 1),
(3, '71.116.127.75', 1),
(3, '150.162.71.34', 1),
(2, '120.114.34.124', 1),
(2, '217.211.41.124', 1),
(2, '123.88.59.30', 2),
(1, '38.151.187.192', 1),
(2, '240.177.24.96', 1),
(1, '21.229.17.241', 2),
(2, '10.201.14.67', 2),
(2, '60.94.22.72', 1),
(1, '31.173.45.239', 1),
(1, '93.236.87.97', 2),
(1, '188.250.113.175', 2),
(2, '218.254.5.254', 1),
(2, '104.148.201.62', 2),
(1, '78.116.167.232', 1),
(3, '235.188.63.46', 1),
(1, '94.211.177.207', 2),
(2, '165.173.17.183', 2),
(2, '124.116.179.206', 1),
(1, '128.42.207.29', 1),
(3, '68.210.228.208', 2),
(2, '165.130.166.222', 1);


--------------------------------------------
-------- OMS - Order management system -----
--  order, shopping cart, address, return --
--------------------------------------------
DROP TABLE IF EXISTS shopping_cart;
CREATE TABLE shopping_cart (
    id SERIAL PRIMARY KEY,
    member_id UUID NOT NULL,
    cart_status cart_status_enum DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO shopping_cart (member_id) VALUES
('a57f32ad-8ebc-486e-940d-7abb2ece682f'),
('bb6604eb-080e-4595-9ae8-32c55cbbb35b'),
('3bbc1316-f71a-4475-9abe-ccf281acdc0b');

DROP TABLE IF EXISTS cart_item;
CREATE TABLE cart_item (
    id SERIAL PRIMARY KEY,
    cart_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    product_name VARCHAR(500),
    product_sku VARCHAR(500),
    product_pic VARCHAR(1000),
    quantity INTEGER CHECK (quantity > 0),
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO cart_item (cart_id, product_id, product_name, product_sku, product_pic, quantity, price)
VALUES
(2, 21, 'Nike Air Max 270', 'NAM270-90', 'nikeairmax270.jpg', 1, 129.99),
(2, 29, 'To Kill a Mockingbird ', 'TKAM', 'tokillamockingbird.jpg', 1, 12.99),

(3, 25, 'Nike Dri-FIT T-Shirt ', 'NDFTS', 'nikeDriFitShirt.jpg', 2, 29.99),
(3, 2, 'iPhone SE', 'IPSE-RED-64', 'iphonese-red.jpg', 1, 449.99),

(5, 5, 'Galaxy S21', 'GS21', 'galaxyS21.jpg', 2, 1099.99),
(5, 19, 'Xbox Series X', 'XSX', 'xboxSeriesX.jpg', 1, 499.99);


DROP TABLE IF EXISTS orders;
CREATE TABLE orders (   -- have to called orders instead of order, or else conflict with ORDER BY
    id SERIAL PRIMARY KEY,
    member_id UUID NOT NULL,
    coupon_id INTEGER DEFAULT NULL,
    order_sn VARCHAR(64),
    member_email VARCHAR(64),
    total_amount DECIMAL(10, 2),
    promotion_amount DECIMAL(10, 2),
    coupon_amount DECIMAL(10, 2),
    discount_amount DECIMAL(10, 2),
    shipping_cost DECIMAL(10, 2),
    pay_amount DECIMAL(10, 2),
    payment_type payment_type_enum NOT NULL,              -- credit card, paypal, google pay
    source_type source_type_enum,
    order_status order_status_enum DEFAULT 'waiting_for_payment',    -- waiting for payment , fulfilling(paid) ,  send , complete(received) , closed(out of return period) ,invalid/cancel
    delivery_company VARCHAR(64),
    delivery_tracking_number VARCHAR(64),
    receiver_phone VARCHAR(32),
    receiver_name VARCHAR(100) NOT NULL,
    receiver_detail_address VARCHAR(200),
    receiver_city VARCHAR(32),
    receiver_state VARCHAR(32),
    receiver_zip_code VARCHAR(32),
    payment_id VARCHAR(32),                  -- store sale id if transaction complete for refund later, or token for pay later
    payer_id VARCHAR(32),
    payment_time TIMESTAMP,                  --
    delivery_time TIMESTAMP,                 -- TBD by UPS api/label and added in
    receive_time TIMESTAMP DEFAULT NULL,      -- update it after UPS said received, should be using redis to do this
    comment VARCHAR(200) DEFAULT NULL,        -- comment left customer like "leave the package under the rug"
    admin_note VARCHAR(500) DEFAULT NULL,       -- note left by previous admin stating what's happening
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO orders (member_id, coupon_id, order_sn, total_amount, promotion_amount, coupon_amount, discount_amount, shipping_cost, pay_amount,
                    payment_type, source_type, order_status, delivery_company, delivery_tracking_number,
                    receiver_name, receiver_phone, member_email, receiver_detail_address, receiver_city, receiver_state, receiver_zip_code,
                    payment_time, delivery_time, comment)
VALUES
('a57f32ad-8ebc-486e-940d-7abb2ece682f', 1, '1001', 2499.98, 149.99, 15, 164.99, 0, 2334.99, 'credit_card', 'web', 'waiting_for_payment', 'UPS', '1234567890',
'Jane Doe', '123-456-7890', 'john@example.com', '123 Main St', 'San Francisco', 'California', '12345',
'2024-04-25 08:30:00', NULL, 'Please include stickers'),

-- $20 off + 10% off coupon
('bb6604eb-080e-4595-9ae8-32c55cbbb35b', 2, '1002', 2199.98, 20, 217.99, 237.99, 0, 1961.99, 'paypal', 'mobile', 'fulfilling', 'UPS', '9876543210',
'Jane Doe', '555-999-8888', 'janedoe@example.com', '456 Market St', 'San Francisco', 'CA', '94102',
'2024-04-24 09:15:00', NULL, 'no comments'),

('a57f32ad-8ebc-486e-940d-7abb2ece682f', 1, '1003', 1399.99, 100, 15, 115, 0, 1284, 'credit_card', 'web', 'sent', 'UPS', '123456789',
'Jane Doe', '555-123-4567', 'jane_doe@example.com', '123 Main St, Apt 4B', 'New York City', 'New York', '10001',
'2024-01-09 10:45:00', NULL, 'I order it with other item, please ship it together'),

('bb6604eb-080e-4595-9ae8-32c55cbbb35b', NULL, '1004', 129.99, 0, 0, 0, 0, 129.99, 'credit_card', 'mobile', 'complete', 'UPS', '987654321',
'John Smith', '555-987-6543', 'john_smith@example.com', '456 Oak St, Apt 12C', 'Los Angeles', 'California', '90001',
'2024-01-12 11:15:00', NULL, NULL),

('3bbc1316-f71a-4475-9abe-ccf281acdc0b', 3, '1005', 19999.90, 1000, 999999.99, 1000999.99, 0, 0, 'google_pay', 'web', 'closed', 'USPS', '987654321',
'John Smith', '555-987-6543', 'john_smith@example.com', '456 Oak St, Apt 12C', 'Los Angeles', 'California', '90001',
'2024-01-12 11:15:00', NULL, NULL);


-- all the items in one order
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
	id SERIAL PRIMARY KEY,
	order_id INTEGER NOT NULL,
	order_sn VARCHAR(64) NOT NULL,
	product_id INTEGER NOT NULL,
	product_pic VARCHAR(500) DEFAULT NULL,
	product_name VARCHAR(200) DEFAULT NULL,
	product_brand VARCHAR(200) DEFAULT NULL,
	product_sn VARCHAR(64) DEFAULT NULL,
	product_price DECIMAL(10, 2) DEFAULT NULL,
	product_quantity INTEGER DEFAULT NULL,
	product_sku_id INTEGER DEFAULT NULL,
	product_sku_code VARCHAR(50) DEFAULT NULL,
	product_category_id INTEGER DEFAULT NULL,
	promotion_name VARCHAR(200) DEFAULT NULL,
	promotion_amount DECIMAL(10, 2) DEFAULT NULL,
	coupon_amount DECIMAL(10, 2) DEFAULT NULL,               -- the coupon that applied to the product will have it if not just 0
	real_amount DECIMAL(10, 2) DEFAULT NULL      -- final paying price after sale and coupon.
);

INSERT INTO
  order_item (order_id, order_sn, product_id, product_pic, product_name, product_brand, product_sn, product_price, product_quantity,
                product_sku_id, product_sku_code, product_category_id, promotion_name, promotion_amount, coupon_amount, real_amount)
VALUES
-- 1 iphone se red and a macbook, 15$ off
(1, '1001', 2, 'iphoneSE.jpg', 'iPhone SE', 'Apple', 'SN-456', 499.99, 1, 5, 'IPSE-RED-64', 15, 'iphone-SE 10% OFF', 49.99, 0, 450),
(1, '1001', 13, 'macBookPro.jpg', 'MacBook Pro', 'Apple', 'SN-678', 1999.99, 1, 16, 'MBP', 17, 'All laptop 100 off', 100, 0, 1899.99),
-- 2 OnePlus 9, 10 % off
(2, '1002', 4, 'oneplus9Pro.jpg', 'OnePlus 9 Pro', 'OnePlus', 'SN-789', 1099.99, 2, 7, 'OP9P', 15, 'OnePlus product $10 off', 20, 0, 2179.98),
-- xps13 $15 off
(3, '1003', 14, 'xps13.jpg', 'XPS 13', 'Dell', 'SN-456', 1399.99, 1, 17, 'XPS13', 17, 'All laptop 100 off', 100, 0, 1299.99),
-- sneaker, no coupon
(4, '1004', 21, 'nikeairmax270.jpg', 'Nike Air Max 270', 'Nike', 'SN-001', 129.99, 1, 24, 'NAM270-90', 8, '', 0, 0, 129.99),
-- 10 macbook invalid free coupon
(5, '1005', 13, 'macBookPro.jpg', 'MacBook Pro', 'Apple', 'SN-678', 1999.99, 10, 16, 'MBP', 17, 'All laptop 100 off', 1000, 0, 18999.90);


-- update order status history/logs
DROP TABLE IF EXISTS order_change_history;
CREATE TABLE order_change_history (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    update_action TEXT NOT NULL,
    order_status INTEGER NULL DEFAULT NULL,              -- waiting for payment 0, fulfilling 1,  send 2, complete(received) 3, closed(out of return period) 4,invalid 5
    note VARCHAR(500) NULL DEFAULT NULL,
    operator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NULL DEFAULT NULL
);

INSERT INTO order_change_history (order_id, update_action, order_status, note, operator)
VALUES
(1, 'Create', 0, 'Order created', 'John'),
(1, 'Cancel', 0, 'Order auto-cancelled due to payment timeout', 'Alice'),
(2, 'Create', 0, 'Order created', 'Bob'),
(2, 'Fulfill', 1, 'Order started fulfillment', 'Charlie'),
(3, 'Create', 0, 'Order re-created', 'David'),
(3, 'Fulfill', 1, 'Order started fulfillment', 'Eve'),
(4, 'Send', 2, 'Order sent for delivery', 'Frank'),
(4, 'Receive', 3, 'Order received', 'Grace'),
(5, 'Receive', 3, 'Order received', 'Helen'),
(5, 'Close', 4, 'Order closed due to return period expiry', 'Isaac');


DROP TABLE IF EXISTS company_address;               -- your(owner) company/warehouses, where product shipping from.
CREATE TABLE company_address (
    id SERIAL PRIMARY KEY,
    address_name VARCHAR(200) NULL DEFAULT NULL,
    send_status INTEGER NULL DEFAULT NULL,            -- does this location send out product. no -> 0 . yes -> 1
    receive_status INTEGER NULL DEFAULT NULL,         -- does this location receive return item(return center) no -> 0 . yes -> 1
    receiver_name VARCHAR(64) NULL DEFAULT NULL,
    receiver_phone VARCHAR(64) NULL DEFAULT NULL,
    state VARCHAR(64) NULL DEFAULT NULL,
    city VARCHAR(64) NULL DEFAULT NULL,
    zip_code VARCHAR(64) NULL DEFAULT NULL,
    detail_address VARCHAR(200) NULL DEFAULT NULL
);

INSERT INTO company_address(address_name, send_status, receive_status, receiver_name,  receiver_phone, state, city, zip_code, detail_address)
VALUES
('111 over there send out avenue 2nd floor', 1, 0, 'Jun', 1800000000, 'New York', 'New York', 11220, '222 over there avenue 2nd floor, go through the gate in north east corner to unload'),
('222 right here return avenue', 0, 1, 'Jun', 1800000000, 'Nevada', 'Las Vegas', 88901, 'Next to the casino'),
('333 backup warehouse avenue', 1, 1, 'Jun', 1800000000, 'Pennsylvania', 'Philadelphia', 19019, 'Big red sign turn left and ring bell to enter');


--- when admin/operator determined if the product can be return, one item(s) return at a time
DROP TABLE IF EXISTS return_request;
CREATE TABLE return_request  (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    company_address_id INTEGER NOT NULL,                   -- return to you(owner), return center or warehouse
    order_sn VARCHAR(64),
    member_id UUID NOT NULL,
    return_quantity INTEGER,                       -- number of items to be returned
    return_name VARCHAR(100),
    return_phone VARCHAR(100),
    return_status return_status_enum DEFAULT 'waiting_to_be_processed',                -- return status,  waiting to process 0, returning(sending) 1, complete 2, rejected(not matching reason) 3
    handle_time TIMESTAMP,                        -- how long to return this item, e.g 2 weeks to return this or return is voided.
    asking_amount DECIMAL(10, 2),
    refunded_amount DECIMAL(10, 2),
    reason VARCHAR(200),                         -- pre-set reasons
    description VARCHAR(500),
    handle_note VARCHAR(500),                    -- notes from admin to customer or rejection reason
    handle_operator VARCHAR(100),                -- who processed this return
    receive_operator VARCHAR(100),               -- who received the return item
    receive_time TIMESTAMP,
    receive_note VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO return_request (order_id, company_address_id, order_sn, member_id, return_quantity, return_name, return_phone, return_status,
                                 handle_time, asking_amount, reason, description, handle_note, handle_operator, receive_operator,
                                 receive_time, receive_note)
VALUES
(1, 1, '1001', 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 3, 'John Doe', '555-123-4567', 'waiting_to_be_processed',
 NULL, NULL, 'Item damaged upon arrival', 'Received two damaged items in the package.',
 NULL, NULL, NULL, NULL, NULL),
(2, 2, '1002', 'bb6604eb-080e-4595-9ae8-32c55cbbb35b', 1, 'Alice Smith', '555-987-6543', 'returning',
 NULL, NULL, 'Wrong item received', 'Received a different product than what was ordered.',
 NULL, NULL, NULL, NULL, NULL),
(3, 3, '1003', '3bbc1316-f71a-4475-9abe-ccf281acdc0b', 6, 'Mary Johnson', '555-789-1234', 'complete',
 '2024-09-09 14:30:00', 75.99, 'Changed my mind', 'Decided not to keep these items.',
 'Refund processed successfully.', 'AdminUser123', 'WarehouseStaff456',
 '2024-09-10 09:15:00', 'Items received in good condition.'),
(4, 1, '1004', 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 1, 'David Wilson', '555-555-5555', 'rejected',
 NULL, NULL, 'Item does not match the description', 'The product received is not as described on the website.',
 'Rejected due to mismatch.', 'AdminUser789', NULL, NULL, NULL),
(5, 2, '1005', 'bb6604eb-080e-4595-9ae8-32c55cbbb35b', 2, 'Linda Davis', '555-123-7890', 'waiting_to_be_processed',
 NULL, NULL, 'Item arrived late', 'Items arrived after the expected delivery date.',
 NULL, NULL, NULL, NULL, NULL);


DROP TABLE IF EXISTS return_item;
CREATE TABLE return_item (
    id SERIAL PRIMARY KEY,
    return_request_id INTEGER NOT NULL,
    brand_id INTEGER NOT NULL,
    order_id INTEGER NOT NULL,
    order_sn VARCHAR(64),
    product_id INTEGER NOT NULL,
    product_sku VARCHAR(100),
    purchased_price DECIMAL(10, 2),
    quantity INTEGER
);

INSERT INTO return_item (return_request_id, brand_id, order_id, order_sn, product_id, product_sku, quantity)
VALUES
(1, 100, 1001, 'OR123456', 101, 'SKU001', 1),
(1, 100, 1001, 'OR123456', 102, 'SKU002', 2),
(2, 200, 1002, 'OR789012', 201, 'SKU003', 1),
(3, 100, 1003, 'OR456789', 301, 'SKU004', 3),
(3, 100, 1003, 'OR456789', 302, 'SKU005', 2),
(3, 200, 1003, 'OR456789', 303, 'SKU006', 1),
(4, 200, 1004, 'OR987654', 401, 'SKU007', 1),
(5, 300, 1005, 'OR555555', 501, 'SKU008', 2);


DROP TABLE IF EXISTS return_request_picture;
CREATE TABLE return_request_picture (
    id SERIAL PRIMARY KEY,
    return_request_id INTEGER NOT NULL,
    filename VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO return_request_picture (return_request_id, filename)
VALUES
(1, 'https://i.imgur.com/zNGBoLk.jpeg'),
(1, 'https://i.imgur.com/DebpKZa.png'),
(2, 'https://i.imgur.com/UJSZE08.jpeg'),
(3, 'https://i.imgur.com/DCtQmc2.png'),
(4, 'https://i.imgur.com/UHgW2D8.jpeg'),
(5, 'https://i.imgur.com/UHgW2D8.jpeg');


DROP TABLE IF EXISTS return_log;
CREATE TABLE return_log (
    id SERIAL PRIMARY KEY,
    return_request_id INTEGER NOT NULL,
    update_action VARCHAR(100),
    description TEXT,
    operator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO return_log (return_request_id, update_action, description, operator)
VALUES
(1, 'update', 'updating return request', 'Jun'), -- TODO: need to add more logs, might add a enum/type here
(1, 'update', 'updating return request', 'Jun'),
(2, 'closed', 'item received and refunded', 'Jun');


--------------
---  CMS  ----
--------------
-- Content management system
-- articles(Buying Guide, product comparison, other MISC if you're specialized shop),
-- images(Store as images urls but it will be store somewhere else like Amazon s3),
-- and video(as in links(youtube private(unlisted or published in your channel) or public video) or store somewhere)
DROP TABLE IF EXISTS article;
CREATE TABLE article (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL, -- URL-friendly slug
    author_id INTEGER DEFAULT 0,     -- author of the article TODO: could be either admin or member or might just leave name there
    author_name VARCHAR(20) NOT NULL,
    body TEXT NOT NULL,
    publish_status publish_status_enum DEFAULT 'pending',  -- article online status
    lifecycle_status lifecycle_status_enum DEFAULT 'normal',  -- article lifecycle status
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO article (title, slug, author_name, body, publish_status)
VALUES
('Buyer''s guide', 'buyers-guide', 'Jun', 'This article provides a comprehensive guide for buyers.', 'published'),
('Product Comparison', 'product-comparison', 'Jun', 'This article compares different products and their features.', 'published'),
('How to Choose the Right Product', 'how-to-choose-the-right-product', 'Jun', 'This article provides tips on how to choose the right product for your needs.', 'published'),
('Get Free Products', 'get-free-products', 'Jun', 'This article teach you how to exploit website for free products.', 'paused');


DROP TABLE IF EXISTS article_QA;
CREATE TABLE article_QA (
    id SERIAL PRIMARY KEY,
    article_id INTEGER NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO article_QA (article_id, question, answer)
VALUES
(1, 'What is a buyer''s guide?', 'A buyer''s guide is a document or article that provides information about a particular product or service to help potential buyers make informed decisions.'),
(1, 'What should I look for in a buyer''s guide?', 'A good buyer''s guide should provide detailed information about the product or service, including its features, benefits, and drawbacks, as well as pricing and purchasing options.'),
(2, 'Which product is the best?', 'It depends on your needs and preferences. This article provides a comparison of different products and their features to help you make an informed decision.'),
(2, 'What are the key features to look for in a product?', 'The key features to look for in a product depend on what you plan to use it for. This article provides a comparison of different products and their features to help you make an informed decision.'),
(3, 'How can I choose the right product?', 'Choosing the right product depends on your needs, preferences, and budget. This article provides tips on how to choose the right product for your needs.');


DROP TABLE IF EXISTS article_image;
CREATE TABLE article_image (
    id SERIAL PRIMARY KEY,
    article_id INTEGER NOT NULL,
    filename VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO article_image (article_id, filename)
VALUES
(1, 'https://i.imgur.com/FSzSViN.png'),
(1, 'buyer_guide_cover.jpg'),
(1, 'buyer_guide_infographic.png'),
(2, 'product_comparison_table.jpg'),
(3, 'choose_right_product_flowchart.png');


DROP TABLE IF EXISTS article_video;
CREATE TABLE article_video (
    id SERIAL PRIMARY KEY,
    article_id INTEGER NOT NULL,
    url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO article_video (article_id, url)
VALUES
(1, 'https://i.imgur.com/tovhDTo.mp4'),
(2, 'https://youtu.be/dQw4w9WgXcQ');


DROP TABLE IF EXISTS article_change_log;
CREATE TABLE article_change_log (
    id SERIAL PRIMARY KEY,
    article_id INTEGER NOT NULL,
    update_action VARCHAR(255) NOT NULL,
    operator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO article_change_log (article_id, update_action, operator)
VALUES
(1, 'Update', 'John Doe'),
(2, 'Create', 'Alice Smith'),
(3, 'Update', 'Bob Johnson'),
(4, 'Delete', 'Eve Wilson'),
(5, 'Create', 'Charlie Brown'),
(6, 'Update', 'Grace Davis'),
(7, 'Delete', 'Frank Miller'),
(8, 'Create', 'Lucy Adams'),
(9, 'Update', 'David Clark'),
(10, 'Create', 'Sarah White');


--------------
---  SMS  ----
--------------

DROP TABLE IF EXISTS coupon;
CREATE TABLE coupon (
    id SERIAL PRIMARY KEY,
    coupon_target target_type_enum DEFAULT 'specific_item',     -- type of coupon (all, specific brand, specific category, or specific item)
    name VARCHAR(100) NOT NULL,                              -- name of the coupon
    discount_type discount_type_enum DEFAULT 'amount',       -- discount type (amount or percent)
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),      -- amount of discount (must be positive)
    minimum_purchase DECIMAL(10, 2) DEFAULT 0 CHECK (minimum_purchase >= 0), -- minimum purchase to apply the coupon (default is 0, no restriction)
    start_time TIMESTAMP NOT NULL,                           -- coupon start time
    end_time TIMESTAMP NOT NULL CHECK (end_time > start_time), -- ensure end_time is after start_time
    note VARCHAR(200) NULL,                                  -- any additional notes
    count INTEGER DEFAULT 0,                                 -- number of this coupon available
    publish_count INTEGER DEFAULT 0,                         -- number of distributed coupons
    used_count INTEGER DEFAULT 0,                            -- number of used coupons
    code VARCHAR(64) NOT NULL UNIQUE,                        -- coupon code (must be unique)
    publish_status publish_status_enum DEFAULT 'pending'             -- coupon status (active or inactive)
);
-- TODO: make sure free coupon don't go negative, add a constraint in database level and application/service level
INSERT INTO coupon (coupon_target, name, discount_type, amount, minimum_purchase, start_time, end_time, count, publish_count, used_count, code, publish_status)
VALUES
('all', '$15 off whole order', 'amount', 15.00, 50.00, NOW(), NOW() + INTERVAL '1 year', 20, 10, 0, '15OFF', 'published'),
('all', '10% off whole order', 'percent', 10.00, 100.00, NOW(), NOW() + INTERVAL '1 year', 20, 10, 0, '10OFF', 'published'),
('all', 'All free', 'amount', 999999.99, 0.00, NOW(), NOW() + INTERVAL '1 year', 1, 1, 1, 'FREE', 'published'),
('specific_brand', '$50 off Apple product', 'amount', 50.00, 500.00, NOW(), NOW() + INTERVAL '1 year', 1, 1, 0, '50OFFAPPLE', 'published'),
('specific_category', '60% off shirts', 'percent', 60.00, 30.00, NOW(), NOW() + INTERVAL '1 year', 1, 1, 0, '60OFFSHIRTS', 'published'),
('specific_item', '20% off Galaxy S21', 'percent', 20.00, 1000.00, NOW(), NOW() + INTERVAL '1 year', 1, 1, 1, '20OFFS21', 'published');

-- the product that are affected by the coupon, coupon type 1-3 will use this. 0 type will not.
DROP TABLE IF EXISTS coupon_product_relation;
CREATE TABLE coupon_product_relation (
    id SERIAL PRIMARY KEY,
    coupon_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    product_name VARCHAR(100),
    product_sn VARCHAR(100),
    product_sku_code VARCHAR(100)
);

INSERT INTO coupon_product_relation (coupon_id, product_id, product_name, product_sn, product_sku_code)
VALUES
-- $50 off Apple product, '50OFFAPPLE'
(4, 1, 'iPhone 12', 'SN-123', 'IP12-RED-128'),
(4, 1, 'iPhone 12', 'SN-123', 'IP12-WHITE-128'),
(4, 1, 'iPhone 12', 'SN-123', 'IP12-BLACK-128'),
(4, 2, 'iPhone SE', 'SN-456', 'IPSE-RED-64'),
(4, 2, 'iPhone SE', 'SN-456', 'IPSE-BLUE-64'),
(4, 6, 'AirPods Pro', 'SN-234', 'APRO1'),
(4, 7, 'AirPods 2', 'SN-789', 'APO2'),
(4, 9, 'iPad Pro', 'SN-901', 'IPPRO'),
(4, 13, 'MacBook Pro', 'SN-678', 'MBP'),

--  60% off shirts, '60OFFSHIRTS'
(5, 25, 'Nike Dri-FIT T-Shirt', 'SN-002', 'NDFTS'),
(5, 26, 'Calvin Klein Logo T-Shirt', 'SN-008', 'CKLTS-L'),
(5, 26, 'Calvin Klein Logo T-Shirt', 'SN-008', 'CKLTS-M'),
(5, 26, 'Calvin Klein Logo T-Shirt', 'SN-008', 'CKLTS-S'),
(5, 27, 'Adidas Essential Track Pants', 'SN-005', 'AEPTP'),

-- 20% off Galaxy S21', '20OFFS21'
(6, 5, 'Galaxy S21', 'SN-234', 'GS21');


DROP TABLE IF EXISTS coupon_history;
CREATE TABLE coupon_history (
    id SERIAL PRIMARY KEY,
    coupon_id INTEGER NOT NULL,
    member_id UUID NOT NULL,
    order_id INTEGER NOT NULL,
    used_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    code VARCHAR(64) NULL DEFAULT NULL
);

INSERT INTO coupon_history (coupon_id, member_id, order_id, used_time, code)
VALUES
(1, 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 1, '2024-04-25 08:45:00', '15OFF'),
(1, 'bb6604eb-080e-4595-9ae8-32c55cbbb35b', 2, '2024-03-25 08:45:00', '15OFF'),
(3, '3bbc1316-f71a-4475-9abe-ccf281acdc0b', 3, '2024-01-11 10:00:00', 'FREE'),
(3, 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 4, '2024-01-11 10:00:00', 'FREE'),
(4, 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 5, '2024-02-25 08:45:00', '50OFFAPPLE'),
(4, 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 6, '2024-02-25 08:45:00', '50OFFAPPLE'),
(4, 'a57f32ad-8ebc-486e-940d-7abb2ece682f', 7, '2024-02-25 08:45:00', '50OFFAPPLE');


DROP TABLE IF EXISTS coupon_change_log;
CREATE TABLE coupon_change_log (
    id SERIAL PRIMARY KEY,
    coupon_id INTEGER NOT NULL,
    update_action VARCHAR(255) NOT NULL,
    operator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO coupon_change_log (coupon_id, update_action, operator)
VALUES
(1, 'Update', 'John Doe'),
(2, 'Create', 'Alice Smith'),
(3, 'Update', 'Bob Johnson'),
(4, 'Delete', 'Eve Wilson'),
(5, 'Create', 'Charlie Brown'),
(6, 'Update', 'Grace Davis');



DROP TABLE IF EXISTS promotion_sale;
CREATE TABLE promotion_sale (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    slug VARCHAR(100) UNIQUE,
    promotion_type sales_type_enum DEFAULT 'is_on_sale',      -- promotion sale is regular sale or  flash sale, or other type of sale
    promotion_target target_type_enum DEFAULT 'specific_item',      -- discount on 0-> all, 1 -> specific brand,  2-> specific category, 3-> specific item
    discount_type discount_type_enum NOT NULL ,       -- 0 -> by amount, 1->  by percent off
    amount DECIMAL(10, 2),
    publish_status publish_status_enum DEFAULT 'pending',               -- 0 -> not active, 1 -> active is it active
    start_time TIMESTAMP,
    end_time TIMESTAMP,         -- TODO: use a scheduler to end the sale
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TODO: does sale stacks?.  and need a way to check discounts time expiration. maybe redis or spring scheduler/task
INSERT INTO promotion_sale(name, slug, promotion_type, promotion_target, discount_type, amount, publish_status, start_time, end_time)
VALUES
('Every thing 10% off', 'every-thing-10-off', 'is_on_sale', 'all', 'percent', 10, 'pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 month'),
('OnePlus product $10 off', 'oneplus-product-dollar10-off', 'is_on_sale', 'specific_brand', 'amount', 10, 'published', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 month'),
('All laptop $100 off', 'all-laptop-dollar100-off', 'is_on_sale', 'specific_category', 'amount', 100, 'published', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 month'),
('iphone-SE 10% OFF', 'iphone-se-10-off', 'flash_sale', 'specific_item', 'percent', 10, 'published', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 month');

-- products that affected by this promotion
-- user other services to find, brand, category, specific product, or All
DROP TABLE IF EXISTS promotion_sale_product;
CREATE TABLE promotion_sale_product (
    id SERIAL PRIMARY KEY,
    promotion_sale_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    product_sku_code TEXT,
    promotion_price DECIMAL(10, 2) NOT NULL,       -- what the price - promotion sale amount = promotion_price
    promotion_limit_item INTEGER NOT NULL,      -- how many allowed to sell at discount, need to check sku stock
    promotion_limit_per_user INTEGER NOT NULL,       -- number of limit per member/account
    publish_status publish_status_enum DEFAULT 'pending'
);

-- TODO: need to check when stock/sold meet limit before canceling the discount
-- does it have priority for discount? like all > category -> brand -> specific product and do they stack?
INSERT INTO promotion_sale_product (promotion_sale_id, product_id, product_sku_code, promotion_price, promotion_limit_item, promotion_limit_per_user, publish_status)
VALUES
-- All laptop 100 off
(3, 13, 'MBP', 1899.99, 80, 3, 'published'),
(3, 14, 'XPS13', 1299.99, 150, 3, 'published'),
(3, 15, 'TPX1C', 1399.99, 200, 3, 'published'),
(3, 16, 'YC940', 1199.99, 150, 3, 'published'),
(3, 17, 'IPG3', 899.99, 100, 3, 'published'),
(3, 18, 'AM15R5', 1899.99, 80, 3, 'published');

-- update here to make it more consistency, will update using service when E-Com is running
UPDATE product SET on_sale_status = 1, sale_price = 1899.99  WHERE id = 13;
UPDATE product_sku SET promotion_price = 1899.99 WHERE product_id = 13 AND sku_code = 'MBP';

UPDATE product SET on_sale_status = 1, sale_price = 1299.99 WHERE id = 14;
UPDATE product_sku SET promotion_price = 1299.99 WHERE product_id = 14 AND sku_code = 'XPS13';

UPDATE product SET on_sale_status = 1, sale_price = 1399.99  WHERE id = 15;
UPDATE product_sku SET promotion_price = 1399.99 WHERE product_id = 15 AND sku_code = 'TPX1C';

UPDATE product SET on_sale_status = 1, sale_price = 1199.99  WHERE id = 16;
UPDATE product_sku SET promotion_price = 1199.99 WHERE product_id = 16 AND sku_code = 'YC940';

UPDATE product SET on_sale_status = 1, sale_price = 899.99 WHERE id = 17;
UPDATE product_sku SET promotion_price = 899.99 WHERE product_id = 17 AND sku_code = 'IPG3';

UPDATE product SET on_sale_status = 1, sale_price = 1899.99 WHERE id = 18;
UPDATE product_sku SET promotion_price = 1899.99 WHERE product_id = 18 AND sku_code = 'AM15R5';


-- OnePlus product $10 off
INSERT INTO promotion_sale_product (promotion_sale_id, product_id, product_sku_code, promotion_price, promotion_limit_item, promotion_limit_per_user)
VALUES
(2, 13, 'OP9P', 1089.99, 100, 3),
(2, 14, 'OBPRO', 139.99, 150, 3),
(2, 15, 'OPW3', 189.99, 80, 3);

UPDATE product SET on_sale_status = 1, sale_price = 1089.99 WHERE id = 4;
UPDATE product_sku SET promotion_price = 1089.99 WHERE product_id = 4 AND sku_code = 'OP9P';

UPDATE product SET on_sale_status = 1, sale_price = 139.99 WHERE id = 8;
UPDATE product_sku SET promotion_price = 139.99 WHERE product_id = 8 AND sku_code = 'OBPRO';

UPDATE product SET on_sale_status = 1, sale_price = 189.99 WHERE id = 10;
UPDATE product_sku SET promotion_price = 189.99 WHERE product_id = 10 AND sku_code = 'OPW3';


-- iphone-SE 10% OFF
INSERT INTO promotion_sale_product (promotion_sale_id, product_id, product_sku_code, promotion_price, promotion_limit_item, promotion_limit_per_user)
VALUES
(4, 2, 'IPSE-BLUE-64', 449.99, 25, 3),
(4, 2, 'IPSE-RED-64', 449.99, 25, 3);

UPDATE product SET on_sale_status = 1, sale_price = 449.99  WHERE id = 2;
UPDATE product_sku SET promotion_price = 449.99 WHERE product_id = 2 AND sku_code = 'IPSE-BLUE-64';

UPDATE product SET on_sale_status = 1, sale_price = 449.99 WHERE id = 2;
UPDATE product_sku SET promotion_price = 449.99 WHERE product_id = 2 AND sku_code = 'IPSE-RED-64';


-- keep track of changes/update to promotion
DROP TABLE IF EXISTS promotion_sale_log;
CREATE TABLE promotion_sale_log (
    id SERIAL PRIMARY KEY,
    promotion_sale_id INTEGER NOT NULL,
    sale_action TEXT,
    promotion_type INTEGER,    -- discount on 0-> all, 1 -> specific brand,  2-> specific category, 3-> specific item
    discount_type INTEGER,       -- 0 -> by amount, 1->  by percent off
    amount DECIMAL(10, 2),
    operator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO promotion_sale_log(promotion_sale_id, sale_action, promotion_type, discount_type, amount, operator)
VALUES
(1, 'update action', 0, 1, 20, 'admin'),
(2, 'update action', 1, 1, 10, 'admin'),
(3, 'update action', 2, 0, 100, 'admin'),
(4, 'update action', 3, 1, 10, 'admin');



-----------------------
---  Notification  ----
-----------------------
DROP TABLE IF EXISTS email_templates;
CREATE TABLE email_templates (
    id SERIAL PRIMARY KEY,
    service_name email_service_type_enum NOT NULL, -- email template used for which service
    template_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- TODO: service name and template have service name. causing confusion need to change to service_type and template_type
INSERT INTO email_templates (service_name, template_text)
VALUES
('user_service', 'Hello {name}, this is a message for {name} only'),
('user_service_all', 'Hello {name}, this is a message for all customer.'),

('sale_service', 'There is a sale is going on through #{start_time} and #{end_time} on #{sale_name}'),

-- auto send when order is created
('order_service', 'Your order #{order_number} is confirmed. Expected delivery date is #{delivery_date}.'),
('order_service_update', 'Your order #{order_number} has been updated. Current status is #{status}'),
('order_service_return', 'Your return request for #{order_number} have started. Current status is #{status} '),
('order_service_return_update', 'Your return request for #{order_number} have updated. Current status is #{status}');


DROP TABLE IF EXISTS email;
CREATE TABLE email (
    id SERIAL PRIMARY KEY,
    service_type VARCHAR(255),
    action_type VARCHAR(255),
    sender_email VARCHAR(255) NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    body TEXT,
    operator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Admin Actions
INSERT INTO email (service_type, action_type, sender_email, recipient_email, subject, body, operator)
VALUES
('admin', 'product_update', 'admin@example.com', 'user1@example.com', 'Product Update', 'Dear User, we have updated information about a product you purchased.', 'jun'),
('admin', 'message_to_all', 'admin@example.com', 'all_users@example.com', 'Important Announcement', 'Dear Users, we have an important announcement for you. Please read carefully.', 'jun'),
('admin', 'message_to_one', 'admin@example.com', 'user2@example.com', 'Personalized Message', 'Dear User, here is a personalized message just for you.', 'jun'),

-- Sales Actions
('sms', 'new_sale_notification', 'sales@example.com', 'all_users@example.com', 'New Sale Notification', 'Exciting news! We have a new sale happening. Check out the latest deals.', 'system'),

-- Order Actions
('oms', 'new_order_confirmation', 'order@example.com', 'user3@example.com', 'New Order Confirmation', 'Thank you for your new order! Here are the details of your purchase.', 'system'),
('oms', 'return_request_update', 'order@example.com', 'user4@example.com', 'Return Request Update', 'Your return request has been approved. Please follow the instructions to complete the return process.', 'system'),
('oms', 'order_canceled', 'order@example.com', 'user5@example.com', 'Order Canceled', 'We regret to inform you that your order has been canceled. Please contact customer support for more information.', 'system'),

-- User Actions
('ums', 'user_creation', 'user_management@example.com', 'new_user@example.com', 'Welcome to Our Platform', 'Welcome to our platform! We are excited to have you as a new user.', 'system'),
('ums', 'password_reset_request', 'user_management@example.com', 'user6@example.com', 'Password Reset Request', 'You have requested to reset your password. Click the link below to proceed with the password reset process.', 'system');




DROP TABLE IF EXISTS email_templates_history;
CREATE TABLE email_templates_history (
    id SERIAL PRIMARY KEY,
    template_id INTEGER NOT NULL,
    update_action TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operator VARCHAR(100) NOT NULL
);

INSERT INTO email_templates_history (template_id, update_action, operator)
VALUES
(1, 'create', 'Jun'),
(2, 'create', 'Jun'),
(3, 'create', 'Jun'),
(4, 'create', 'Jun'),
(5, 'create', 'Jun'),
(6, 'create', 'Jun'),
(7, 'create', 'Jun');
