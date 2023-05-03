import React, { createRef, PureComponent, useEffect, useRef, useState } from 'react';
import { UIManager, findNodeHandle, NativeEventEmitter, View } from 'react-native';

import { MlkitBarcodeViewManager } from './MlkitBarcodeViewManager';


export const BARCODE = {
  FORMAT_UNKNOWN: -1,
  FORMAT_ALL_FORMATS: 0,
  FORMAT_CODE_128: 1,
  FORMAT_CODE_39: 2,
  FORMAT_CODE_93: 4,
  FORMAT_CODABAR: 8,
  FORMAT_DATA_MATRIX: 16,
  FORMAT_EAN_13: 32,
  FORMAT_EAN_8: 64,
  FORMAT_ITF: 128,
  FORMAT_QR_CODE: 256,
  FORMAT_UPC_A: 512,
  FORMAT_UPC_E: 1024,
  FORMAT_PDF417: 2048,
  FORMAT_AZTEC: 4096,
  //  TYPE_UNKNOWN : 0,
  //  TYPE_CONTACT_INFO : 1,
  //  TYPE_EMAIL : 2,
  //  TYPE_ISBN : 3,
  //  TYPE_PHONE : 4,
  //  TYPE_PRODUCT : 5,
  //  TYPE_SMS : 6,
  //  TYPE_TEXT : 7,
  //  TYPE_URL : 8,
  //  TYPE_WIFI : 9,
  //  TYPE_GEO : 10,
  //  TYPE_CALENDAR_EVENT : 11,
  //  TYPE_DRIVER_LICENSE : 12,
}

const createFragment = (viewId) =>{
  console.log(TAG,"Create Frag ViewId :",viewId);
  UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    "create",
    [viewId]
  );
}

const destroyFragment = (viewId) =>{
  console.log(TAG,"Destroy Frag ViewId :",viewId);
  UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    "destroy",
    [viewId]
  );
}

const eventEmitter = new NativeEventEmitter();

const TAG = "MlkitBarcodeView: ";

export class MlkitBarcodeView extends PureComponent {

  constructor(props) {
    super(props);
    this.state = {
      enableScan: this.props.enableScanner,
    }
    this.ref = createRef();
  }

  componentDidMount() {
    console.log(TAG, "componentDidMount.");
    if (this.props.enableScanner)
      setTimeout(this.startScanner, 200)
      // this.startScanner();
  }
  componentWillUnmount() {
    // Here goes the code you wish to run on unmount
    console.log(TAG,"WillUnmount.");
    // const viewId = findNodeHandle(this.ref.current);
    // destroyFragment(viewId);
    // this.setState({enableScan:false});
    this.eventListener && this.eventListener.remove();
  }

  UNSAFE_componentWillReceiveProps(newProps) {
      if (newProps.enableScanner && !this.state.enableScan) {
        this.setState({ enableScan: true }, () => {
          // console.log(TAG,"Starting Scanner enableScan");
          setTimeout(this.startScanner, 400)
        })
      }
      else if (!newProps.enableScanner && this.state.enableScan) {
        this.setState({enableScan:newProps.enableScanner});
      }

      //BarCode updated restart scanner.
      if(this.props.enableScanner && newProps.barcodeFormat != this.props.barcodeFormat){
        this.setState({enableScanner:false},()=>{
          setTimeout(()=>{
            this.setState({enableScan:true},()=>{
              // console.log(TAG,"Starting Scanner BarcodeFormate Changed");
              setTimeout(this.startScanner, 400);
            });
          },700);
        });
      }

      this.props = newProps;
  }

  startScanner = () => {
    // console.log(TAG, "startScanner.... enableScan :"+this.state.enableScan);
    this.eventListener = eventEmitter.addListener('BARCODE_SCANNED', (event) => {
      console.log(event) // "someValue"
      if (this.props.onSuccess)
        this.props.onSuccess(event)
    });

    const viewId = findNodeHandle(this.ref.current);
    createFragment(viewId);

  }



  render() {
    if (this.state.enableScan) {
      return (
        <View style={{  }}>
          <MlkitBarcodeViewManager
            {...this.props}
            ref={this.ref}
          />
        </View>
      );
    }
    else
      return null;
  }

};