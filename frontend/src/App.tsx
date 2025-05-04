import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import HomePage from './pages/HomePage';
import CallbackPage from './pages/CallbackPage';
import { AuthProvider } from './context/AuthContext';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/callback" element={<CallbackPage />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
