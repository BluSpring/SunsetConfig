# Sunset Config
Yeah, another configuration library, as if we don't already have enough of those. But wait a minute,
what is that? We're using codecs instead?!

The name was chosen because of the term "the sun is setting", which came up because as my brain was coming up with clever names that
were synonyms of "configuration", settings came to mind and suddenly [SettingSunset](https://www.twitch.tv/settingsunset_)
did too. Not affiliated (or friends even) with them, I just follow them on some socials,
though I'd imagine they're a pretty cool person to be around.

This config library aims to make serialization/deserialization easy by using Minecraft's codec system,
while also providing some nice utilities that can allow creating a nice GUI for it.

## What makes this library different?
Imagine having all the control over whatever you serialize, and none of it at the same time.

Codecs are insanely powerful, you can do a lot with them, but at the same time, they are not documented
at all within Mojang's codebase despite being open sourced, so you will have no idea what you're doing
if you are new to this. I recommend reading through [Fabric's wiki on codecs](https://docs.fabricmc.net/develop/codecs)
to get a general idea of how to get by, then you'll slowly start to understand what I mean by all the control,
and yet none of it at the same time.

Sunset does provide some utility functions that will allow you to get by easier for the more simple things
that most people would need, but other than that, you're pretty much on your own. Good luck.

## How on earth do I use this thing?
So, for starters, you'll want to set up your buildscript. Let me get you started.

`build.gradle.kts`
```kts
repositories {
    maven("https://mvn.devos.one/releases")
}

dependencies {
    val sunsetVersion = "1.0.0" // Check the releases for the latest version.
    api("xyz.bluspring.sunset:sunset-config:$sunsetVersion")
}
```

Now, for actually making the config, here's how you can get started:
```kt
object YourConfig {
    var yourStringValue = "You can easily define a string like so!"
    var yourIntValue = 12 // And with this, an integer value!
    var yourCustomValue = listOf(12)
}

val config = SunsetConfig.create(
    // Define your config file's path here.
    Path("config.json"),
    
    // By default, we provide JSON serialization via Gson, 
    // however you may provide your own serializer if you wish.
    JsonSerializer()
) {
    // You can create a category like so. It can even be nested as much as you'd like.
    category("category_id") {
        // This creates a new config value with the ID "your_string_value" that will be saved from
        // and loaded to whenever you modify your config values in YourConfig.
        // The second argument will allow Sunset to modify your config value when it is loaded,
        // and will additionally allow Sunset to access the config value when it is requested to
        // save the config data.
        // Sunset will automatically use the value that is already defined in your existing
        // config value as the default value to be used.
        string("your_string_value", YourConfig::yourStringValue)
    }
    
    // You don't even necessarily need a category either!
    // This works similarly to the string setup above, and also allows you to specify the range
    // you want your integer to be in.
    integer("your_int_value", min = 0, max = 12, YourConfig::yourIntValue)
    
    // Not enough to work with? Not to worry, that is exactly what the codecs are for!
    // For more information on how codecs work, you may consult the FabricMC wiki on codecs:
    // https://docs.fabricmc.net/develop/codecs
    value("your_custom_value", Codec.list(Codec.INT), YourConfig::yourCustomValue)
}

// Now you'll want to load your config from its existing file. Sunset does not implicitly
// load the config, you will have to handle it yourself.
// Don't worry, it won't throw an exception if your config file does not exist.
config.load()

// And now you can save it whenever!
config.save()
```