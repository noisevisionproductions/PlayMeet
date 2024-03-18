One boring day, I had the urge to play Tennis, but nobody from my friends list was available that
day. Back then I was struggling to find portfolio ideas for potentially my first job as a software
developer in Java. Then, it appeared in my head to create an Android app called:

# PlayMeet

The goal was to help users search for people for sports games in real life, whether it is a user's
first-time, amateur, or advanced level.
Since this is my first big project I decided to create it sometime around October, it took me some
time to develop. It was a bumpy road because I had no experience in Android and front-end
development, but in the end, I learned a lot.

# Features

For now, it allows users to create an account, and create a post that contains information about the
game, like location, date, and number of people needed. Then, other users can use filters that I
created, to find more specific requirements he's looking for, and sign in to the post that he find
interesting. Users can also send a message to each other in real-time, thanks to Firebase Realtime
Database, which allows for real-time data syncing.
While this is a fully working version, I will create new features with time.

# Installation

It took me about a month to understand the Google Play console and learn about the process of app
publishing, but finally, my app is available at Google Play:
https://play.google.com/store/apps/details?id=com.noisevisionproductions.playmeet

# Development History

This project has instilled in me the belief in coding, that whatever I wish to create can be
achieved. Also, given our times, I will admit that in some parts throughout my coding experience, I
used Chat-GPT, but more like a tutor, who could explain to me how something worked, or why or when
to choose a given approach. In the end, my goal is to learn new things in Java, and among others, I
can achieve that easier.

## Design

At the beginning of my app development, progress was very slow. I had to learn from scratch
everything about Android SDK. For the first time, I had to learn how .xml files work, their
functionality, and usage. Lately, I discovered that there also exists Jetpack Compose, but for this
project, I will stay with the classic choice. I realized that front-end is not my strong suit, and
this is a field that requires a lot of time to understand but I was trying my best anyway. One day
my close friend explained to me how colors and their shades in design work. The first design of the
post about the game was without BottomSheetDialogFragment. But when I realized that when a user will
need more information about a given game, it just simply won't fit, that's why I decided to use the
BottomSheetDialogFragment class.
For vector icons, I used the site https://www.freepik.com/ which contains free VGA files. The logo
is a part of a larger image that DALLE created for me.

## Database

Thanks to this project, I finally realized how databases work in a real-time environment and that
not every database can be suitable in a given situation. I had to learn from my mistakes.
My first choice was Realm. Although I loved the simplicity of its filtering capabilities, I
struggled with managing background operations. However, after working with it for some time, I
finally understood that this particular database was primarily designed for offline usage.
I was trying to set up Realm Sync, but after many synchronization issues, I decided to change the
database to Firebase Realtime Database. Step by step, adapting my code for the new DB, I was working
along that one for long time and I used it for every part in my project that required data storage.
When I had fully functional chat rooms with messages and a place where I could place posts created
by users, I realized that my filtering logic was very poor, because it was filtering all the posts
on the client side. It was a bad choice because when there would be potentially many posts, the
performance would be very slow. Realtime Database from Firebase doesn't allow for filtering more
than one query. And it was essential in my situation. I wanted the user to filter posts by multiple
queries, like location and sport type at the same time.
Since I was already in the Firebase environment, I chose the Firestore Database, which allows for
querying the content just before it is downloaded and shown in the app and it was significantly
better.
In the end, I'm using a Realtime Database for storing user information, chat rooms, and messages,
and Firestore for posts and registrations for posts. Along with that, I'm using Firebase
Authentication for user authentication.

# License and Use Restrictions

This project is for portfolio viewing purposes only. All rights are reserved under copyright law.
The following restrictions apply:

No Use: This project and its contents may not be used, copied, modified, merged, published,
distributed, sublicensed, and/or sold without the express permission of the author.

No Modification: The code and associated documentation files may not be modified in any way.

Viewing Only: This project is made available for viewing purposes only as part of the author's
portfolio. It is intended to showcase the author's coding skills and capabilities to potential
employers or clients.

By accessing and viewing this project, you acknowledge and agree that you have read and understand
these restrictions, and agree to be bound by them. If you do not agree with these terms, you are
prohibited from using or accessing this site and its code.

<<<<<<< HEAD
"All Rights Reserved"
=======
"All Rights Reserved"
>>>>>>> 3158d87615899215dc5785e7ceb6785d9e26c7f5
