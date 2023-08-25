# MinecraftMinigameLib

## Installation

### Plugin Side
Add the following to the `maven-shade-plugin` config:
```xml
<configuration>
    ...
    <shadedArtifactAttached>true</shadedArtifactAttached>
    <shadedClassifierName>shaded</shadedClassifierName>
    <relocations>
        <relocation>
            <pattern>org.kcsup.minecraftminigamelib</pattern>
            <shadedPattern>${groupId}.${artifactId}.lib.minecraftminigamelib</shadedPattern>
        </relocation>
    </relocations>
    <outputFile>target/${name}.jar</outputFile>
    ...
</configuration>
```
Add the following to the plugin dependencies:
```xml
<dependencies>
    ...
    <dependency>
        <groupId>org.kcsup</groupId>
        <artifactId>minecraftminigamelib</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>compile</scope>
    </dependency>
    ...
</dependencies>
```

*Still testing deployment
