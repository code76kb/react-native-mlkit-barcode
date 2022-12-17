import * as React from 'react';

import { StyleSheet, TouchableOpacity, View, Text, SafeAreaView } from 'react-native';
import { MlkitBarcodeView } from 'react-native-mlkit-barcode';

export default class App extends React.Component {

  constructor(props) {
    super(props)
    this.state = {
      enableQrScanner: false,
    }
  }

  render() {
    return (
      <SafeAreaView style={{flex:1}}>
        <MlkitBarcodeView
          enableQrScanner={this.state.enableQrScanner}
          style={{
            width: 900,
            height: 900,
            marginVertical: 20,
          }}
          onSuccess={(data) => {
            console.log("App BarCode On Success :", data);
            this.setState({enableQrScanner:false})
          }}
        />

        <TouchableOpacity style={{width:100, height:50,}} onPress={()=>this.setState({enableQrScanner:!this.state.enableQrScanner})}>
          <Text style={{textAlign:'center',color:'red', fontSize:20, backgroundColor:'black'}}>{"Toggle SCanner"}</Text>
        </TouchableOpacity>    
        </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
