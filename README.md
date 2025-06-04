# 🚍 Transit-Sync

![Platform](https://img.shields.io/badge/Platform-Android-blue.svg)  
![Tech](https://img.shields.io/badge/Backend-Firebase-orange.svg)  
![Language](https://img.shields.io/badge/Language-Java-yellow.svg)  
![UI](https://img.shields.io/badge/UI-XML-green.svg)  
![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)

**Transit-Sync** is an Android-based mobile application designed to provide real-time transit information, helping users track buses, view routes, and manage their profiles. With a user-friendly interface, secure authentication, and Firebase integration, this app simplifies navigation for daily commuters and transit enthusiasts alike.

---

## 🚀 Features  

### 🚌 Bus Tracking & Routes
- View real-time bus locations and estimated arrival times.
- Browse detailed bus routes and stop information.
- Filter buses by route, location, or schedule.

### 🔒 User Authentication
- Secure sign-up and login using **Firebase Authentication**.
- Password recovery for account safety.

### 🗺️ Route Details
- Explore detailed route maps and stop schedules.
- Access bus details like speed and capacity through an intuitive UI.

### 👤 Profile Management
- Create and customize your user profile.
- Save favorite routes and view transit history.

### 🖥️ Personalized Dashboard
- Manage saved routes and preferences in one place.
- Quick access to frequently used features.

---

## 🛠️ Tech Stack  

- **Frontend**: Java, XML  
- **Backend**: Firebase (Authentication, Realtime Database)  
- **Tools**: Android Studio  

---

## 📂 Project Structure

```
TransitSync/
└── app/
    └── src/
        └── main/
            ├── java/
            │   └── com/example/transitsync/
            │       ├── MainActivity.java         # Entry point and main home screen
            │       ├── models/                   # Bus.java, BusDetails.java
            │       ├── adapters/                 # BusAdapter.java for UI lists
            │       ├── utils/                    # BusLocation.java, BusStopsManager.java
            └── res/
                ├── drawable/                     # Icons, backgrounds
                ├── layout/                       # activity_main.xml, fragment_detail.xml, etc.
                ├── values/                       # strings.xml, styles.xml, colors.xml
                └── values-night/                 # Dark mode resources
```

---

## 📲 Installation & Setup

### Prerequisites
- Android Studio installed
- Firebase project setup

### Steps
1. **Clone the repository**
   ```sh
   git clone https://github.com/Preveen369/TransitSync.git
   ```
2. **Open in Android Studio** and sync dependencies.
3. **Configure Firebase**:
   - Add `google-services.json` to `app/` directory.
   - Enable Firebase Authentication & Realtime Database.
4. **Run the app** on an emulator or a physical device.

---

## 🤝 Contributing
Pull requests are welcome! Feel free to **fork the repository** and submit improvements.

### Contributions are welcome! Follow these steps:
1. **Fork the project.**
2. **Create a feature branch:**
   ```sh
   git checkout -b feature-name
   ```
3. **Commit your changes:**
   ```sh
   git commit -m "Add feature description"
   ```
4. **Push to the branch:**
   ```sh
   git push origin feature-name
   ```
5. **Open a pull request.**

---

## 📧 Contact
For queries or suggestions:
- 📧 Email: spreveen123@gmail.com
- 🌐 LinkedIn: www.linkedin.com/in/preveen-s-17250529b/

---

## 🌟 Show your support
If you find this project interesting, please consider giving it a ⭐ on GitHub to show your support!
