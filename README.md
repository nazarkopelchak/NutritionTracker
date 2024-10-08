# NutritionTracker

## Overview

The NutritionTracker app allows users to search for various food items and their nutritional values, such as calories, protein, sugar, fat, thanks to the EDAMAM database. If the product is not in the database,
the user has the option to use a _custom mode_, that allows the user manually enter nutritional values of the food products. All nutritional data is stored locally,
and can be reset daily at the specific time at the user's discretion (this feature must be enabled in the Settings screen to work). Reset data will be stored on the History screen where
users can access it at any time.

## Screenshots

<img src="/screenshots/home_screen_screenshot.png" width=280 height=556><img src="/screenshots/add_screenshot1.png" width=280 height=556><img src="/screenshots/add_screenshot2.png" width=280 height=556><img src="/screenshots/history_screenshot.png" width=280 height=556>

## Requirements

### Android Studio
- You need to have the Android Studio installed if you want to run or edit code. If you don't have it installed, you can get it [here](https://developer.android.com/studio)

### Internal App Sharing
- If you want to install the app on your smartphone, you will need an Android powered device and Internal App Sharing enabled in Google Play Store
- To enable Internal App Sharing, follow these steps below:
  - Open Play Store
  - Navigate to Settings (Click on the account profile picture, which is located at the top right corner of the screen)
  - Enable Developer Options (Go to > **About** section and tap 7 times on the **Play Store version** option)
  - Once you get the "You are now a developer!" prompt, you will now see the **Developer options** section in the **Settings>General**
  - Navigate to Developer options, and enable **Internal app sharing** switch.
  - Done

## Installation

### Android Studio

- Open Android Studio
- Go to File or use shortcut Alt + \
- Select New -> Project from Version Control
- Paste this project's web URL into the URL field, select a desired directory to save the project and click Clone
- The installation will take a few minutes (depends on your machine). Once the installation is complete, you can launch the NutritionTracker app.

### Internal App Sharing
- Make sure you have **Internal app sharing** option enabled on your device
- Use the link below to download the NutritionTracker app to your device
- [https://play.google.com/apps/test/RQteTJFlqI8/ahAO29uNTGxI2dSgitZqV-gLa0bcuhC2Tt2Nvyl8bcwqfD2SQus5reojaD2PXmBur7iA5Ps0-HH7gnc8Yh1f2BQL7e](https://play.google.com/apps/test/RQteTJFlqI8/ahAO29uNTGxI2dSgitZqV-gLa0bcuhC2Tt2Nvyl8bcwqfD2SQus5reojaD2PXmBur7iA5Ps0-HH7gnc8Yh1f2BQL7e)

### Google Play Store
The NutritionTracker app is not available for public download at this time. I'm working on uploading it to the Play Store.

## Current Features

- Searching for nutritional values
- Manually entering nutritional values
- Displaying total daily food products and their nutritional values
- Resetting total nutritional values daily at the specified time
- Setting total nutrititional values

## Tools used

- Kotlin
- Jetpack Compose
- Room Database
- Retrofit (REST API)
- Hilt Dependency Injection

## Testing
If you would like to become a Nutrition Tracker tester, contact me at nazar1999pro@gmail.com.

## Support

If there are any problems with the intallation or the app, contact me at nazar1999pro@gmail.com.
