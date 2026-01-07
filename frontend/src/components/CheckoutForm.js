import React, { useState } from 'react';
import { createPaymentOrder, processPayment } from '../services/api';
import '../styles/CheckoutForm.css';

const CheckoutForm = ({ orderId, setOrderId }) => {
  const [amount, setAmount] = useState('');
  const [currency, setCurrency] = useState('INR');
  const [paymentMethod, setPaymentMethod] = useState('UPI');
  const [cardNumber, setCardNumber] = useState('');
  const [cardExpiry, setCardExpiry] = useState('');
  const [cardCvv, setCardCvv] = useState('');
  const [upiId, setUpiId] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleCreateOrder = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const response = await createPaymentOrder({
        merchantId: 1,
        amount: parseFloat(amount),
        currency: currency
      });
      setOrderId(response.orderId);
      setSuccess('Order created successfully!');
    } catch (err) {
      setError('Failed to create order: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    const paymentData = {
      orderId: orderId,
      paymentMethod: paymentMethod,
      amount: parseFloat(amount),
      currency: currency
    };

    if (paymentMethod === 'CARD') {
      paymentData.cardNumber = cardNumber;
      paymentData.cardExpiry = cardExpiry;
      paymentData.cardCvv = cardCvv;
    } else if (paymentMethod === 'UPI') {
      paymentData.upiId = upiId;
    }

    try {
      const response = await processPayment(paymentData);
      setSuccess('Payment successful! Transaction ID: ' + response.transactionId);
      setError('');
    } catch (err) {
      setError('Payment failed: ' + err.message);
      setSuccess('');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="checkout-container">
      {!orderId ? (
        <form onSubmit={handleCreateOrder} className="checkout-form">
          <h2 data-test-id="checkout-title">Create Payment Order</h2>
          
          <div className="form-group">
            <label htmlFor="amount">Amount</label>
            <input
              id="amount"
              type="number"
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
              data-test-id="amount-input"
            />
          </div>

          <div className="form-group">
            <label htmlFor="currency">Currency</label>
            <select
              id="currency"
              value={currency}
              onChange={(e) => setCurrency(e.target.value)}
              data-test-id="currency-select"
            >
              <option value="INR">INR</option>
              <option value="USD">USD</option>
              <option value="EUR">EUR</option>
            </select>
          </div>

          <button type="submit" disabled={loading} data-test-id="create-order-button">
            {loading ? 'Creating...' : 'Create Order'}
          </button>
        </form>
      ) : (
        <form onSubmit={handlePayment} className="checkout-form">
          <h2 data-test-id="payment-title">Complete Payment</h2>
          <p>Order ID: <span data-test-id="order-id">{orderId}</span></p>
          
          <div className="form-group">
            <label htmlFor="paymentMethod">Payment Method</label>
            <select
              id="paymentMethod"
              value={paymentMethod}
              onChange={(e) => setPaymentMethod(e.target.value)}
              data-test-id="payment-method-select"
            >
              <option value="UPI">UPI</option>
              <option value="CARD">Card</option>
            </select>
          </div>

          {paymentMethod === 'CARD' && (
            <>
              <div className="form-group">
                <label htmlFor="cardNumber">Card Number</label>
                <input
                  id="cardNumber"
                  type="text"
                  value={cardNumber}
                  onChange={(e) => setCardNumber(e.target.value)}
                  placeholder="1234 5678 9012 3456"
                  required
                  data-test-id="card-number-input"
                />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="cardExpiry">Expiry (MM/YY)</label>
                  <input
                    id="cardExpiry"
                    type="text"
                    value={cardExpiry}
                    onChange={(e) => setCardExpiry(e.target.value)}
                    placeholder="12/25"
                    required
                    data-test-id="card-expiry-input"
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="cardCvv">CVV</label>
                  <input
                    id="cardCvv"
                    type="text"
                    value={cardCvv}
                    onChange={(e) => setCardCvv(e.target.value)}
                    placeholder="123"
                    required
                    data-test-id="card-cvv-input"
                  />
                </div>
              </div>
            </>
          )}

          {paymentMethod === 'UPI' && (
            <div className="form-group">
              <label htmlFor="upiId">UPI ID</label>
              <input
                id="upiId"
                type="text"
                value={upiId}
                onChange={(e) => setUpiId(e.target.value)}
                placeholder="user@upi"
                required
                data-test-id="upi-id-input"
              />
            </div>
          )}

          <button type="submit" disabled={loading} data-test-id="pay-button">
            {loading ? 'Processing...' : 'Pay'}
          </button>
        </form>
      )}

      {error && <div className="error-message" data-test-id="error-message">{error}</div>}
      {success && <div className="success-message" data-test-id="success-message">{success}</div>}
    </div>
  );
};

export default CheckoutForm;
