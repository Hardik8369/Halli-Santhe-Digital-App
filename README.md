\# 🌾 Halli Santhe Digital

\### \*Your Local Market, Now Digital!\*



\[!\[Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)

\[!\[Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)

\[!\[Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com)

\[!\[License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)



\---



\## 📱 About the App



\*\*Halli Santhe Digital\*\* is an Android marketplace app that digitizes the traditional rural market \*(Halli Santhe)\* experience. It connects local artisans and small-scale sellers with buyers through a simple, intuitive platform built for rural and semi-urban India.



> 🏆 Built as part of the \*\*Android App Development Using Gen AI\*\* course at Mindmatrix LMS.



\---



\## 📸 Screenshots



| Splash | Login | Buyer Home |

|--------|-------|------------|

| !\[Splash](screenshots/splash.jpg) | !\[Login](screenshots/login.jpg) | !\[Buyer](screenshots/buyer.jpg) |



| Seller Dashboard | Product Detail |

|-----------------|---------------|

| !\[Seller](screenshots/seller.jpg) | !\[Product](screenshots/product.jpg) |



\---



\## ✨ Features



\### 👨‍🌾 For Sellers

\- 📦 List products with name, price, category, description \& image

\- 🤖 AI-powered product description generation (Gemini API)

\- 🖼️ Image upload via Cloudinary

\- 📊 Dashboard showing total product count

\- 🗑️ Delete their own products

\- 👤 Edit profile (name, phone, address)



\### 🛒 For Buyers

\- 🔍 Browse all local products in a clean grid layout

\- 🔎 Search products by name in real-time

\- 🏷️ Filter by category: Food, Craft, Textile, Other

\- 📋 View detailed product information

\- 📲 Contact seller directly via \*\*WhatsApp\*\*

\- 👤 Edit profile (name, phone, address)



\### 🔐 Authentication

\- Email \& Password login/signup

\- Google Sign-In

\- Role selection at signup: \*\*Buyer\*\* or \*\*Seller\*\*

\- Password reset via email

\- Persistent login session



\---



\## 🛠️ Tech Stack



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



\---



\## 📸 App Flow



```

Splash Screen

&#x20;    ↓

Login / Signup (Email or Google)

&#x20;    ↓

Role Selection (Buyer / Seller)

&#x20;    ↓

Buyer Home ──────────── Seller Dashboard

(Browse products)       (Manage products)

&#x20;    ↓                        ↓

Product Detail           Add Product

(WhatsApp contact)       (AI Description)

&#x20;    ↓

Profile (Edit \& Logout)

```



\---



\## 🚀 Getting Started



\### Prerequisites

\- Android Studio (latest)

\- JDK 11+

\- Firebase project set up

\- Cloudinary account



\### Setup



1\. \*\*Clone the repository\*\*

```bash

git clone https://github.com/Hardik8369/Halli-Santhe-Digital-App.git

cd Halli-Santhe-Digital-App

```



2\. \*\*Add your `google-services.json`\*\*

&#x20;  - Go to \[Firebase Console](https://console.firebase.google.com)

&#x20;  - Download `google-services.json`

&#x20;  - Place it in the `app/` folder



3\. \*\*Add API keys to `local.properties`\*\*

```properties

GEMINI\_API\_KEY=your\_gemini\_api\_key\_here

```



4\. \*\*Build and run\*\*

```bash

./gradlew installDebug

```



\---



\## 📁 Project Structure



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

&#x20;   ├── Constants.kt

&#x20;   └── CloudinaryConfig.kt

```



\---



\## 🔒 Security



\- API keys are stored in `local.properties` (not committed to Git)

\- Firebase security rules protect database access

\- `google-services.json` is restricted to the app's package name



\---



\## 🌱 Future Scope



\- 💳 In-app UPI/Razorpay payment integration

\- 📦 Order management and tracking

\- 🔔 Push notifications

\- ⭐ Seller ratings and reviews

\- 🌐 Multi-language support (Kannada, Hindi)

\- 📊 Analytics dashboard for sellers



\---



\## 👨‍💻 Developer



\*\*Hardik Gowda\*\*

\- GitHub: \[@Hardik8369](https://github.com/Hardik8369)



\---



\## 📄 License



This project is licensed under the MIT License.



\---



<p align="center">🌾 Supporting Local Artisans, One Product at a Time 🌾</p>

