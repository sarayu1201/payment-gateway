import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const createPaymentOrder = async (orderData) => {
  const response = await axios.post(`${API_BASE_URL}/payments/create-order`, orderData);
  return response.data;
};

export const processPayment = async (paymentData) => {
  const response = await axios.post(`${API_BASE_URL}/payments/process`, paymentData);
  return response.data;
};

export const getPaymentStatus = async (paymentId) => {
  const response = await axios.get(`${API_BASE_URL}/payments/${paymentId}`);
  return response.data;
};
