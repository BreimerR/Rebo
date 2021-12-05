# Rebo

Rebo is a database writer.

## Versions
current = :1.0.1531-SNAPSHOT
### Version Naming
[1] = rebo main release   
[2] = rebo bug fixes  
[1531] = kotlin version i.e 1.5.31 

<a href="https://www.paypal.com/donate/?hosted_button_id=CUHRL6CUYWRTA" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>

## End Goal

Have a CrossPlatform database

## Options

1. [Kodein-DB](https://github.com/Kodein-Framework) (Future)
2. [Exposed](https://github.com/JetBrains/Exposed) Currently Supported

## Dependencies

[BuildSrc](https://github.com/BreimerR/BuildSrc.git) // Easy to use gradle scripts

> Run

1. ```git clone https://github.com/BreimerR/BuildSrc.git```
2. ```cd BuildSrc```
3. ```build publishMavenLocal```
4. ```git clone https://github.com/BreimerR/languages.git```
5. ```cd languages```
6. ```build publishMavenLocal```

> Do the same for the other local dependencies strings and collection extensions
>> https://github.com/BreimerR/lazy  
> > https://github.com/BreimerR/strings


> Maven Posting
>> Maven Postings haven't been done yet
> > But will be in the next release.

## Features

1. Generate
    1. Exposed Tables
    2. Exposed Daos
    3. Data class extension functions
2. Generate database migration Base code
    1. Generate For Exposed database
    2. ...

## Usage

### Adding to your project

1. Clone repo

```markdown
git clone https://github.com/BreimerR/Rebo.git 
```

2. Build repo and publish to maven local

```shell
cd Rebo
build publishMavenLocal
```

3. Add mavenLocal to your repositories

```kotlin
repositories {
    // ...
    mavenLocal()
}
```

4. Add dependencies to your project
```
dependencies{
   add("kspJvm","libetal.kotlinx.ksp.plugins:rebo:1.0.1531-SNAPSHOT")
}
```

#### Simple Entity Class

```kotlin
@Entity
data class User(
    @Column
    val name: String,
    @Column
    @PrimaryKey
    val id: Int = 0
)
```

#### With Foreign Key Table

```kotlin
@Entity
data class Account(
    @Column
    @ForeignKey
    val user: User,
    @Column
    @PrimaryKey
    val id: Int = 0
)
```

#### With Annotated Foreign Key
This will work the same way. 
Would lack some of the arguments used in @ForeignKey though
```kotlin
@Entity
data class Account(
    @Column
    val user: User,
    @Column
    @PrimaryKey
    val id: Int = 0
)
```