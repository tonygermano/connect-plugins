# BasicHttpsConfiguration

This is a zero-config drop in replacement for the DefaultHttpConfiguration that comes with open source mirth (nextgen) connect. When this extension is installed, all HTTP Listeners will use SSL. The plugin reads your mirth.properties file and uses the same certificate, protocols, and cyphers that are configured for the web server running on the Admin port.

## Requirements
In order to build, you must place these mirth jars in a lib subdir,      `mirth-server.jar`, `mirth-client-core.jar`, `donkey-server.jar`, `http-server.jar`, and `http-shared.jar` from your mirth release are needed for this project. The mirth and donkey files are located in `server-lib`, and the http files are in `extensions/http` in your mirth install directory. See `build.gradle` for more details.

## Installation

Build with
```bash
./gradlew build
```

This will produce a `.zip` file in `build/dist`.

You can then install the zip file through the extensions screen in the Administrator.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[MPL-2.0](https://mozilla.org/MPL/2.0/)