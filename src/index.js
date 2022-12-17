import React, { createRef, PureComponent, useEffect, useRef, useState } from 'react';
import { UIManager, findNodeHandle, NativeEventEmitter,View } from 'react-native';

import { MlkitBarcodeViewManager } from './MlkitBarcodeViewManager';

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

export class MlkitBarcodeView extends PureComponent {

  constructor(props) {
    super(props);
    this.state = {
      enableQr: this.props.enableQrScanner,
    }
    this.ref = createRef();
  }

  componentDidMount() {
   if(this.props.enableQrScanner)
      this.startScanner();
  }
  componentWillUnmount() {
    // Here goes the code you wish to run on unmount
    // console.log("MlkitBarcodeView: Unmounting...");
    this.eventListener.remove();
    // destroyFragment(viewId);
  }

  componentWillReceiveProps(newProps) {
    this.props = newProps;
    // console.log("MlkitBarcodeView :enableQrScanner :" + this.props.enableQrScanner);
    if (this.props.enableQrScanner) {
      this.setState({enableQr:true},()=>{
        setTimeout(this.startScanner,400)
      })

    }
    else {
    this.stopScanner();
    }
  }

  startScanner=()=>{
    this.eventListener = eventEmitter.addListener('BARCODE_SCANNED', (event) => {
      console.log(event) // "someValue"
      if (this.props.onSuccess)
        this.props.onSuccess(event)
    });

    const viewId = findNodeHandle(this.ref.current);
    createFragment(viewId);

  }

  stopScanner=()=>{
  // this.eventListener.remove();
  const viewId = findNodeHandle(this.ref.current);
  destroyFragment(viewId);
  setTimeout(() =>this.setState({enableQr:false}), 400);
  }


  render() {
    if (this.state.enableQr) {
      return (
        <View style={{...this.props.style}}>
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