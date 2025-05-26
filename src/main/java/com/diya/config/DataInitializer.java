//
//package com.ecommerce.config;
//
//import com.ecommerce.exception.InsufficientStockException;
//import com.ecommerce.model.*;
//import com.ecommerce.repository.*;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final CategoryRepository categoryRepository;
//    private final ProductRepository productRepository;
//    private final AboutPageRepository aboutPageRepository;
//    private final BlogPostRepository blogPostRepository;
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        log.info("Initializing data...");
//
//        if (userRepository.count() == 0) {
//            initializeUsers();
//        }
//
//        if (categoryRepository.count() == 0) {
//            initializeCategories();
//        }
//
//        if (productRepository.count() == 0) {
//            initializeProducts();
//        }
//
//        if (aboutPageRepository.count() == 0) {
//            initializeAboutPage();
//        }
//
//        if (blogPostRepository.count() == 0) {
//            initializeBlogPosts();
//        }
//
//        log.info("Data initialization completed.");
//    }
//
//    private void initializeUsers() {
//        log.info("Initializing users...");
//
//        // Create admin user
//        User admin = User.builder()
//                .firstName("Admin")
//                .lastName("User")
//                .email("admin@example.com")
//                .password(passwordEncoder.encode("admin123"))
//                .role(User.Role.ADMIN)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        // Create customer user
//        User customer = User.builder()
//                .firstName("John")
//                .lastName("Doe")
//                .email("john@example.com")
//                .password(passwordEncoder.encode("password123"))
//                .role(User.Role.CUSTOMER)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        // Create carts for users
//        Cart adminCart = new Cart();
//        adminCart.setUser(admin);
//        admin.setCart(adminCart);
//
//        Cart customerCart = new Cart();
//        customerCart.setUser(customer);
//        customer.setCart(customerCart);
//
//        userRepository.saveAll(List.of(admin, customer));
//        log.info("Users initialized successfully.");
//    }
//
//    private void initializeCategories() {
//        log.info("Initializing categories...");
//
//        // Root categories
//        Category electronics = Category.builder()
//                .name("Electronics")
//                .slug("electronics")
//                .description("Electronic devices and gadgets")
//                .build();
//
//        Category clothing = Category.builder()
//                .name("Clothing")
//                .slug("clothing")
//                .description("Apparel and fashion items")
//                .build();
//
//        Category books = Category.builder()
//                .name("Books")
//                .slug("books")
//                .description("Books and publications")
//                .build();
//
//        categoryRepository.saveAll(List.of(electronics, clothing, books));
//
//        // Subcategories for Electronics
//        Category smartphones = Category.builder()
//                .name("Smartphones")
//                .slug("smartphones")
//                .description("Mobile phones and smartphones")
//                .parent(electronics)
//                .build();
//
//        Category laptops = Category.builder()
//                .name("Laptops")
//                .slug("laptops")
//                .description("Laptop computers")
//                .parent(electronics)
//                .build();
//
//        Category accessories = Category.builder()
//                .name("Accessories")
//                .slug("accessories")
//                .description("Electronic accessories")
//                .parent(electronics)
//                .build();
//
//        // Subcategories for Clothing
//        Category mens = Category.builder()
//                .name("Men's")
//                .slug("mens")
//                .description("Men's clothing")
//                .parent(clothing)
//                .build();
//
//        Category womens = Category.builder()
//                .name("Women's")
//                .slug("womens")
//                .description("Women's clothing")
//                .parent(clothing)
//                .build();
//
//        // Subcategories for Books
//        Category fiction = Category.builder()
//                .name("Fiction")
//                .slug("fiction")
//                .description("Fiction books")
//                .parent(books)
//                .build();
//
//        Category nonFiction = Category.builder()
//                .name("Non-Fiction")
//                .slug("non-fiction")
//                .description("Non-fiction books")
//                .parent(books)
//                .build();
//
//        categoryRepository.saveAll(List.of(smartphones, laptops, accessories, mens, womens, fiction, nonFiction));
//        log.info("Categories initialized successfully.");
//    }
//
//    private void initializeProducts() {
//        log.info("Initializing products...");
//
//        Category smartphones = categoryRepository.findBySlug("smartphones")
//                .orElseThrow(() -> new RuntimeException("Category not found"));
//
//        Category laptops = categoryRepository.findBySlug("laptops")
//                .orElseThrow(() -> new RuntimeException("Category not found"));
//
//        Category mens = categoryRepository.findBySlug("mens")
//                .orElseThrow(() -> new RuntimeException("Category not found"));
//
//        Category womens = categoryRepository.findBySlug("womens")
//                .orElseThrow(() -> new RuntimeException("Category not found"));
//
//        Category fiction = categoryRepository.findBySlug("fiction")
//                .orElseThrow(() -> new RuntimeException("Category not found"));
//
//        // Smartphones
//        Product iphone = Product.builder()
//                .name("iPhone 14 Pro")
//                .slug("iphone-14-pro")
//                .description("Apple's flagship smartphone with cutting-edge features")
//                .price(new BigDecimal("999.99"))
//                .stock(50)
//                .category(smartphones)
//                .images("https://placeholder.com/products/iphone14pro.jpg")
//                .featured(true)
//                .build();
//
//        Product samsung = Product.builder()
//                .name("Samsung Galaxy S23")
//                .slug("samsung-galaxy-s23")
//                .description("Premium Android smartphone with excellent camera")
//                .price(new BigDecimal("899.99"))
//                .stock(45)
//                .category(smartphones)
//                .images("https://placeholder.com/products/galaxys23.jpg")
//                .featured(true)
//                .build();
//
//        // Laptops
//        Product macbook = Product.builder()
//                .name("MacBook Pro 16")
//                .slug("macbook-pro-16")
//                .description("Powerful laptop for professionals")
//                .price(new BigDecimal("2499.99"))
//                .stock(25)
//                .category(laptops)
//                .images("https://placeholder.com/products/macbookpro.jpg")
//                .featured(true)
//                .build();
//
//        Product dell = Product.builder()
//                .name("Dell XPS 15")
//                .slug("dell-xps-15")
//                .description("High-performance Windows laptop")
//                .price(new BigDecimal("1799.99"))
//                .stock(30)
//                .category(laptops)
//                .images("https://placeholder.com/products/dellxps15.jpg")
//                .featured(false)
//                .build();
//
//        // Men's clothing
//        Product mensTshirt = Product.builder()
//                .name("Men's Basic T-Shirt")
//                .slug("mens-basic-tshirt")
//                .description("Comfortable cotton t-shirt for everyday wear")
//                .price(new BigDecimal("29.99"))
//                .stock(100)
//                .category(mens)
//                .images("https://placeholder.com/products/menstshirt.jpg")
//                .featured(false)
//                .build();
//
//        // Women's clothing
//        Product womensDress = Product.builder()
//                .name("Women's Summer Dress")
//                .slug("womens-summer-dress")
//                .description("Elegant and comfortable summer dress")
//                .price(new BigDecimal("59.99"))
//                .stock(80)
//                .category(womens)
//                .images("https://placeholder.com/products/womensdress.jpg")
//                .featured(true)
//                .build();
//
//        // Fiction book
//        Product novel = Product.builder()
//                .name("The Great Novel")
//                .slug("the-great-novel")
//                .description("Bestselling fiction novel by renowned author")
//                .price(new BigDecimal("19.99"))
//                .stock(150)
//                .category(fiction)
//                .images("https://placeholder.com/products/greatnovel.jpg")
//                .featured(false)
//                .build();
//
//        productRepository.saveAll(List.of(iphone, samsung, macbook, dell, mensTshirt, womensDress, novel));
//        log.info("Products initialized successfully.");
//    }
//
//    private void initializeAboutPage() {
//        log.info("Initializing about page...");
//
//        AboutPage aboutPage = AboutPage.builder()
//                .title("About Our Store")
//                .content("<h1>Welcome to Our E-Commerce Store</h1><p>We are dedicated to providing quality products at competitive prices. Our mission is to offer exceptional customer service and a seamless shopping experience.</p><p>Founded in 2023, we've quickly grown to become a trusted name in online retail.</p>")
//                .active(true)
//                .build();
//
//        aboutPageRepository.save(aboutPage);
//        log.info("About page initialized successfully.");
//    }
//
//    private void initializeBlogPosts() {
//        log.info("Initializing blog posts...");
//
//        User admin = userRepository.findByEmail("admin@example.com")
//                .orElseThrow(() -> new RuntimeException("Admin user not found"));
//
//        BlogPost post1 = BlogPost.builder()
//                .title("Welcome to Our New Online Store")
//                .slug("welcome-new-store")
//                .content("<p>We're thrilled to announce the launch of our brand new online store! After months of hard work and preparation, we're excited to bring you a seamless shopping experience with a wide range of products.</p><p>Whether you're looking for the latest electronics, stylish clothing, or enriching books, we've got you covered. Our user-friendly interface makes shopping a breeze, and our secure checkout process ensures your information is always protected.</p>")
//                .author(admin)
//                .published(true)
//                .publishedAt(LocalDateTime.now().minusDays(7))
//                .tags("announcement,news")
//                .build();
//
//        BlogPost post2 = BlogPost.builder()
//                .title("Summer Collection Now Available")
//                .slug("summer-collection")
//                .content("<p>Beat the heat with our new summer collection! We've curated a selection of products perfect for the sunny season, from lightweight clothing to portable electronics.</p><p>Our fashion experts have handpicked the trendiest styles to keep you looking your best throughout the summer. And our tech team has selected the best gadgets to enhance your summer adventures, whether you're hitting the beach or embarking on a road trip.</p>")
//                .author(admin)
//                .published(true)
//                .publishedAt(LocalDateTime.now().minusDays(3))
//                .tags("summer,collection,fashion")
//                .build();
//
//        BlogPost post3 = BlogPost.builder()
//                .title("Tech Trends to Watch in 2023")
//                .slug("tech-trends-2023")
//                .content("<p>The tech world is constantly evolving, and 2023 is shaping up to be an exciting year for innovation. From AI advancements to new smartphone features, here's what to expect in the coming months.</p><p>Artificial intelligence continues to transform user experiences, making devices smarter and more intuitive. Foldable screens are becoming more mainstream, offering versatility without compromising on quality. And sustainable tech is gaining momentum as more manufacturers prioritize eco-friendly materials and processes.</p>")
//                .author(admin)
//                .published(true)
//                .publishedAt(LocalDateTime.now().minusDays(1))
//                .tags("technology,trends,innovation")
//                .build();
//
//        blogPostRepository.saveAll(List.of(post1, post2, post3));
//        log.info("Blog posts initialized successfully.");
//    }
//}
