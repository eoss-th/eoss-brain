# BRAINY

![alt text](https://lh3.googleusercontent.com/WXb1GJKCf3CIFO9cGE7BKPtCgneFKLoOnJCnQWwfZimo05DmKtTnH_A6CbXQNqoIxQ=w300)

Project BRAINY is the open source general AI with dynamic neural networks written in Java (jdk 8) and distributed under the liberal [MIT license](LICENSE).
You can use it to support the chatbot messenging api for facebook or your own channel (see https://www.eoss-th.com).
BRAINY can run in many environment such as heroku, google appengine (java 8) including Android (Voice also support).

# Getting Started

## Gradle
```
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.eoss-th:eoss-brain:master'
}
```
## Maven
```
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.eoss-th</groupId>
    <artifactId>brainy</artifactId>
    <version>master</version>
</dependency>
```
# Example


```
Endpoint endpoint = new ConsoleEndpoint("examples/ocm");

Scanner scanner = new Scanner(System.in, "UTF-8");

while(true) {
    System.out.print("Me:>>");
    System.out.print(endpoint.process(scanner.nextLine()));
    System.out.println();
}
```
