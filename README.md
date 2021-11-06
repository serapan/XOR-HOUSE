# XOR-House
Software Engineering Project: Price comparison through crowdsourcing

## Μέλη τις ομάδας XOR-House:

-  Αναστάσιος Μπακιρτζής (el15144)
-  Διονύσιος Σπηλιόπουλος (el15195)
-  Δανάη Ευσταθίου (el15122)
-  Σεραφείμ Παναγιωτίδης (el15131)
-  Γραμματική Τσακουμάγκου (el15755)
-  Κάλια Επισκόπου (el09787)

## Οδηγίες εγκατάστασης και deployment του project

### Εγκατάσταση του Git:

#### Σε UNIX/Linux:

`sudo apt-get install git`

#### Σε Windows:

Το παρακάτω link θα κατεβάσει τον installer του Git:

[Git Installer Download](https://git-scm.com/download/win)

Ανοίξτε τον  Installer και ακολουθήστε τις οδηγίες (αφήστε όλες τις επιλογές στα default).

### Java JDK:

Κατεβάστε το JDK 8u191 για το δικό σας λογισμικό (να θυμάστε που αποθηκεύτηκε ο φάκελος)

[JDK 8 Download](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Ενδεχομένως να πρέπει να κάνετε Expand το παραπάνω αρχείο.


### Gradle:

Μπορείτε να κατεβάσετε το gradle από το παρακάτω link:

[Gradle Download](https://gradle.org/next-steps/?version=5.2.1&format=all)

Αποσυμπιέστε το παραπάνω αρχείο σε φάκελο της επιλογής σας:

`mkdir /opt/gradle`
`unzip -d /opt/gradle gradle-5.2.1-bin.zip`

Εισάγετε το gradle στο PATH ώστε να μπορούν να το βρουν η εφαρμογές:

`export PATH=$PATH:/opt/gradle/gradle-5.2.1/bin`

### MySQL:

Εάν το λογισμικό σας είναι windows ή macosx αρκεί να κατεβάσετε τον αντίστοιχο [installer](https://dev.mysql.com/downloads/mysql/).

Για linux κάνετε τα παρακάτω:

`sudo apt-get update`
`sudo apt-get install mysql-server`

Εκκίνηση του mySQL server:

`systemctl start mysql`

Εκκίνηση του mySQL Shell:

`/usr/bin/mysql -u root -p`

Δημιουργία root διαχειριστή της βάσης:

`UPDATE mysql.user SET authentication_string = PASSWORD('password') WHERE User = 'root';`
`FLUSH PRIVILEGES;`

Δημιουργία βάσης για την εφαρμογή:

`CREATE DATABASE cheapest;`

Έξοδος από το mySQL sheel:

`quit`

### Python:

Η python χρησιμοποιείτε και για την αυτοματοποιημένη δημιουργία .sql αρχείων αλλά και κατά την εκτέλεση της εφαρμογής κατά τις διαδικασίες log in/ log out. Για τον λόγο αυτόν απαιτητέ η ύπαρξη της python 2.7 στον υπολογιστή σας. Κατεβάστε και εγκαταστήστε την python 2.7 μαζί με το [miniconda](https://conda.io/en/latest/miniconda.html).

#### Packages:

##### mysql-connector

`conda install mysql-connector`

##### bcrypt

`conda install bcrypt`

#### Ενεργοποίηση του περιβάλλοντος της python 2.7:

`conda activate base`


### IntelliJ:

Δημιουργίστε ένα "φοιτητικό" λογαριασμό στο site της JetBrains (πατήστε το παρακάτω link) με email αυτό του πολυτεχνείου:

[JetBrains Student Application](https://www.jetbrains.com/shop/eform/students)

Θα σας έρθει ένα email επιβεβαίωσης.

Κατεβάστε και εγκαταστήστε το IntelliJ Free Trial (για Windows επιλέξτε .exe):

[IntelliJ Download](https://www.jetbrains.com/idea/download)

Στην εγκατάσταση αφήστε όλες τις επιλογές default.

Ενεργοποιήστε το Intellij Ultimate με τον λογαριασμό που φτιάξετε προηγουμένως.

Είναι προφανές ότι οποιοδήποτε editor ή IDE είναι αρκετό για την ανάπτυξη αυτής της εφαρμογής. Το intellij είναι κάτι που εμείς επιλέξαμε να χρησημοποιήσουμε.


### Project Checkout from Git:

Σε ένα terminal πλοηγηθείτε στο directory που θέλετε να εγκαταστήσετε την εφαρμογή:

`cd Desktop`

Εκτελέστε την εντολή clone του git:

`git clone https://github.com/serapan/XOR-HOUSE.git`

Θα δημιουργηθεί ο φάκελος XOR-House με τον πηγαίο κώδικα της εφαρμογής.

Με την παρακάτω εντολή μπορείτε να ελέγχετε για ενημερώσεις της:

`git pull origin master`

### Project Setup:

Το βασικό configuration file βρίσκετε στο path: XOR-House/src/main/resources/application.properties.

Σε αυτό μπορείτε να βάλετε τα στοιχεία (name, uname, password) για την σύνδεση με την βάση, να ορίσετε σε πιο port θα τρέχει ο server και να ρυθμίσετε το επίπεδο του logging και των αναφορών που θα γίνονται.

Για την λειτουργία της εφαρμογής απαιτείτε ή εισαγωγή της παρακάτω συνάρτησης στην βάση:

`/XOR-House/src/main/resources/sql/distanceSphericalLaw.sql`  

Η εισαγωγή μπορεί να γίνει από προγράμματα διαχείρισης βάσεων δεδομένων τύπου Workbench ή και από το mySQL shell:

`mysql -u username -p password cheapset < distanceSphericalLaw.sql`

Πέρα από την παραπάνω συνάρτηση δεν απαιτητέ κάτι άλλο για την βάση. Το schema και η εισαγωγή προϊόντων γίνετε με αυτοματοποιημένο τρόπο κατά την έναρξη τις εφαρμογής.

#### Google API Key Credentials

Στην εφαρμογή μας χρησιμοποιείτε το cloud vision api της google για τον λόγο αυτό για την λειτουργία της εφαρμογής απαιτείται να αποθηκεύσουμε μια νέα environment variable που δείχνει το path του json file που περιέχει το API Key.

####Προσωρινή Λύση:

`export GOOGLE_APPLICATION_CREDENTIALS=.../XOR-House/src/main/resources/apiGoogleKey.json`

####Καλύτερη Λύση:

##### Linux
`sudo -H gedit /etc/environment`

Type your password

Edit the text file just opened:

`GOOGLE_APPLICATION_CREDENTIALS=".../XOR-House/src/main/resources/"`

##### Mac 

Εισάγετε την εντολή της προσωρινής λύσης στο ".bash_profile" αρχείο.



### Run Application/ Server

Εάν όλα πήγαν καλά το project είναι έτοιμο για να γίνει deployed. 

Πλοηγηθήτε στο φάκελο που κάνατε στο προηγούμενο βήμα clone:

`cd Desktop/XOR-House`

Για να κάνετε deploy την εφαρμογή (build/ run) αρκεί να εκτελέσετε την παρακάτω εντολή:

`./gradlew bootrun`

Ανοίγοντας έναν Browser και πηγαίνoντας στην διεύθυνση:  https://localhost:8765/ πρέπει να εμφανιστεί η ιστοσελίδα μας. 

### Run Integrated Tests:

Εάν όλα πήγαν καλά το project είναι έτοιμο για να γίνει deployed. 

Πλοηγηθήτε στο φάκελο που κάνατε στο προηγούμενο βήμα clone:

`cd Desktop/XOR-House`

Για να κάνετε deploy την εφαρμογή (build/ run) αρκεί να εκτελέσετε την παρακάτω εντολή:

`./gradlew test`

## Εργαλεία που χρησιμοποιήθηκαν για την ανάπτυξη της εφαρμογής:


- IntelliJ
- Workbench
- Postman

## Εργαλεία που χρησιμοποιήθηκαν για την υλοποίηση της εφαρμογής:

### Back-end RESTful API

- Gradle
- JAVA
- SpringBoot
- Python
- Cloud Vision API

### Front-end

- html5
- bootstrap
- custom css
- javascript
- google maps and places api
- AngularJS
- FusionCharts 
