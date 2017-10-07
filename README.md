# eoss-brain

eoss-brain is the open source chatbot framework written in Java (jdk 7) and distributed under the liberal [MIT license](LICENSE). 
You can use it to support the chatbot messenging api for facebook and line.
eoss-brain can run in many environment such as heroku, google appengine (java 7) including Android. 

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
    <artifactId>eoss-brain</artifactId>
    <version>master</version>
</dependency>
```
# Example

You can see the example usage at https://github.com/eoss-th/eoss-brain/blob/master/src/test/java/com/eoss/brain/ContextTestCase.java

```
Context context = new FileContext("john");
Session session = new Session(context);
session.learning = true;
new WakeupCommandNode(session).execute(null);

Scanner scanner = new Scanner(System.in, "UTF-8");

while(true) {
  System.out.print("Me:>>");
  System.out.println("John:>>" + session.parse(MessageObject.build(template, scanner.nextLine())));
}
```
