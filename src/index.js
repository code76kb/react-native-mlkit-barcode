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

const createFragment = (viewId) =>
  UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    "create",
    [viewId]
  );

const destroyFragment = (viewId) =>
  UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    "destroy",
    [viewId]
  );

const eventEmitter = new NativeEventEmitter();

const TAG = "MlkitBarcodeView: ";

export class MlkitBarcodeView extends PureComponent {

  constructor(props) {
    super(props);
    this.state = {
      enableQr: this.props.enableQrScanner,
    }
    this.ref = createRef();
  }

  componentDidMount() {
    // console.log(TAG, "componentDidMount...");
    if (this.props.enableQrScanner)
      this.startScanner();
  }
  componentWillUnmount() {
    // Here goes the code you wish to run on unmount
    // console.log("MlkitBarcodeView: Unmounting...");
    this.eventListener && this.eventListener.remove();
    // destroyFragment(viewId);
  }

  componentWillReceiveProps(newProps) {
    // console.log(TAG, "Will receiveProps  :enableQrScanner this.Props :"+ JSON.stringify(this.props) +" new Props"+JSON.stringify(newProps)+ " enableQr :" + this.state.enableQr);
      if (newProps.enableQrScanner && !this.state.enableQr) {
        this.setState({ enableQr: true }, () => {
          setTimeout(this.startScanner, 400)
        })
      }
      else if (!newProps.enableQrScanner && this.state.enableQr) {
        this.stopScanner();
      }

      //BarCode updated restart scanner.
      if(this.props.enableQrScanner && newProps.barcodeFormat != this.props.barcodeFormat){
        this.stopScanner();
        setTimeout(()=>{
          this.setState({enableQr:true},()=>{
            setTimeout(this.startScanner, 400);
          });
        },700);
      }

      this.props = newProps;
  }

  startScanner = () => {
    // console.log(TAG, "startScanner....");
    this.eventListener = eventEmitter.addListener('BARCODE_SCANNED', (event) => {
      console.log(event) // "someValue"
      if (this.props.onSuccess)
        this.props.onSuccess(event)
    });

    const viewId = findNodeHandle(this.ref.current);
    createFragment(viewId);

  }

  stopScanner = () => {
    // console.log(TAG, 'stopScanner...');
    // this.eventListener.remove();
    const viewId = findNodeHandle(this.ref.current);
    destroyFragment(viewId);
    setTimeout(() => this.setState({ enableQr: false }), 400);
  }


  render() {
    if (this.state.enableQr) {
      return (
        <View style={{ position: 'absolute', width: 0, height: 0 }}>
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