# 🌾 Halli Santhe Digital
### *Your Local Market, Now Digital!*

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 📱 About the App

**Halli Santhe Digital** is an Android marketplace app that digitizes the traditional rural market *(Halli Santhe)* experience. It connects local artisans and small-scale sellers with buyers through a simple, intuitive platform built for rural and semi-urban India.

> 🏆 Built as part of the **Android App Development Using Gen AI** course at Mindmatrix LMS.

---

## ✨ Features

### 👨‍🌾 For Sellers
- 📦 List products with name, price, category, description & image
- 🤖 AI-powered product description generation (Gemini API)
- 🖼️ Image upload via Cloudinary
- 📊 Dashboard showing total product count
- 🗑️ Delete their own products
- 👤 Edit profile (name, phone, address)

### 🛒 For Buyers
- 🔍 Browse all local products in a clean grid layout
- 🔎 Search products by name in real-time
- 🏷️ Filter by category: Food, Craft, Textile, Other
- 📋 View detailed product information
- 📲 Contact seller directly via **WhatsApp**
- 👤 Edit profile (name, phone, address)

### 🔐 Authentication
- Email & Password login/signup
- Google Sign-In
- Role selection at signup: **Buyer** or **Seller**
- Password reset via email
- Persistent login session

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| Platform | Android (Min SDK 24) |
| Authentication | Firebase Auth |
| Database | Firebase Firestore |
| Image Storage | Cloudinary |
| AI Integration | Google Gemini 1.5 Flash |
| Image Loading | Glide |
| HTTP Client | OkHttp3 |
| UI Framework | Material Design 3 |
| Build System | Gradle (Kotlin DSL) |

---

## 📸 App Flow

```
Splash Screen
     ↓
Login / Signup (Email or Google)
     ↓
Role Selection (Buyer / Seller)
     ↓
Buyer Home ──────────── Seller Dashboard
(Browse products)       (Manage products)
     ↓                        ↓
Product Detail           Add Product
(WhatsApp contact)       (AI Description)
     ↓
Profile (Edit & Logout)
```

---

## 📷 App Screenshots

### 🌾 Splash Screen
<img width="250" height="250" alt="slpash jpg" src="https://github.com/user-attachments/assets/b526c8ec-b57c-4ad7-9bdb-167c4c61fcb6" />
  
### 🔐 Login Screen
<img width="250" height="250" alt="login jpg" src="https://github.com/user-attachments/assets/8d4625f2-c948-4c26-b892-0e41ad89b2f5" />

### 🛒 Buyer Home
<img  width="250" height="250" alt="buyer jpg" src="https://github.com/user-attachments/assets/d2993867-78ed-43f0-8cd6-719f0da99ab6" />

### 👨‍🌾 Seller Dashboard
<img  width="250" height="250" alt="seller jpg" src="https://github.com/user-attachments/assets/25f2b2cd-176f-4dbe-8ac2-a1a0e4e4b90d" />

### 📄 Product Detail Page
<img  width="250" height="250" alt="Seller Dashboard jpg" src="https://github.com/user-attachments/assets/e0ec5c79-0cf6-4a5f-9623-0399b8d1035f" />

### ➕ Add Product Screen
<img width="250" height="250" alt="image jpg" src="https://github.com/user-attachments/assets/8ac474c8-950c-404f-97f4-57a7305c7ce9" />

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest)
- JDK 11+
- Firebase project set up
- Cloudinary account

### Setup

1. **Clone the repository**
```bash
git clone https://github.com/Hardik8369/Halli-Santhe-Digital-App.git
cd Halli-Santhe-Digital-App
```

2. **Add your `google-services.json`**
   - Go to [Firebase Console](https://console.firebase.google.com)
   - Download `google-services.json`
   - Place it in the `app/` folder

3. **Add API keys to `local.properties`**
```properties
GEMINI_API_KEY=your_gemini_api_key_here
```

4. **Build and run**
```bash
./gradlew installDebug
```

---

## 📁 Project Structure

```
app/src/main/java/com/mindmatrix/hallisanthedigital/
├── adapter/
│   └── ProductAdapter.kt
├── model/
│   └── Product.kt
├── ui/
│   ├── auth/
│   │   ├── LoginActivity.kt
│   │   └── SignupActivity.kt
│   ├── buyer/
│   │   └── BuyerHomeActivity.kt
│   ├── seller/
│   │   └── SellerDashboardActivity.kt
│   ├── addproduct/
│   │   └── AddProductActivity.kt
│   ├── productdetail/
│   │   └── ProductDetailActivity.kt
│   ├── profile/
│   │   └── ProfileActivity.kt
│   └── splash/
│       └── SplashActivity.kt
└── utils/
    ├── Constants.kt
    └── CloudinaryConfig.kt
```

---

## 🔒 Security

- API keys are stored in `local.properties` (not committed to Git)
- Firebase security rules protect database access
- `google-services.json` is restricted to the app's package name

---

## 📌 Challenges Faced

- Integrating Firebase Authentication with role-based login flow
- Managing image uploads efficiently using Cloudinary
- Implementing real-time product search and category filtering
- Handling AI-generated product descriptions using Gemini API
- Designing a clean and simple UI suitable for mobile users
- Maintaining separate workflows for buyers and sellers

## 🌱 Future Scope

- 💳 In-app UPI/Razorpay payment integration
- 📦 Order management and tracking
- 🔔 Push notifications
- ⭐ Seller ratings and reviews
- 🌐 Multi-language support (Kannada, Hindi)
- 📊 Analytics dashboard for sellers

---

## 👨‍💻 Developer

**Hardik Gowda**
- GitHub: [@Hardik8369](https://github.com/Hardik8369)

---

## 📄 License

This project is licensed under the MIT License.

---

<p align="center">🌾 Supporting Local Artisans, One Product at a Time 🌾</p>
this is my readme file in github here for thsi read me file i want to add that photos without changinganything
