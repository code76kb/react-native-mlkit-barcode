# react-native-mlkit-barcode
Google mlkit based barcode-qr code scanner
## Installation

```sh
npm install react-native-mlkit-barcode
```

## Usage

```js
import { MlkitBarcodeView } from "react-native-mlkit-barcode";

// ...

 <MlkitBarcodeView
          enableQrScanner={this.state.enableQrScanner}
          style={{
            width: 900,
            height: 900,
          }}
          onSuccess={(data) => {
            console.log("BarCode :", data);
            this.setState({enableQrScanner:false})
          }}
        />
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
