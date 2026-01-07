import React, { useState } from 'react';
import CheckoutForm from './components/CheckoutForm';
import './styles/App.css';

function App() {
  const [orderId, setOrderId] = useState(null);
  
  return (
    <div className="App">
      <header className="App-header">
        <h1>Payment Gateway - Hosted Checkout</h1>
      </header>
      <main>
        <CheckoutForm orderId={orderId} setOrderId={setOrderId} />
      </main>
    </div>
  );
}

export default App;
