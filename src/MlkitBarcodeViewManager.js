import {
  requireNativeComponent,
  UIManager,
  Platform,
} from 'react-native';
import PropTypes from 'prop-types';

const LINKING_ERROR =
  `The package 'react-native-mlkit-barcode' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

MlkitBarcodeProps = {
  barcodeFormat: PropTypes.number,
};

const ComponentName = 'MlkitBarcodeViewManager';
 
export const MlkitBarcodeViewManager =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent(ComponentName)
    : () => {
      throw new Error(LINKING_ERROR);
    };
