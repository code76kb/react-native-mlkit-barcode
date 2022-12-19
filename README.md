# react-native-mlkit-barcode
Google mlkit based barcode-qr code scanner
## Installation

```sh
npm install react-native-mlkit-barcode
```

## Usage

```js
import { MlkitBarcodeView, BARCODE } from "react-native-mlkit-barcode";

// ...

 <MlkitBarcodeView
          enableQrScanner={this.state.enableQrScanner}
          barcodeFormat={BARCODE.FORMAT_ALL_FORMATS}
          onSuccess={(data) => {
            console.log("App BarCode On Success :", data);
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
