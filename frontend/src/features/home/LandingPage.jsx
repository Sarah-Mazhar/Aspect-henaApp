import React, { useEffect, useState } from 'react';
import './LandingPage.css';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { FaBolt, FaChartLine, FaCloud, FaMagic } from 'react-icons/fa';

const words = ['Efficient', 'Scalable', 'Digitized', 'Simplified'];

const featureItems = [
  { icon: <FaBolt className="feature-icon" />, label: 'Efficient' },
  { icon: <FaChartLine className="feature-icon" />, label: 'Scalable' },
  { icon: <FaCloud className="feature-icon" />, label: 'Digitized' },
  { icon: <FaMagic className="feature-icon" />, label: 'Simplified' },
];

const LandingPage = () => {
  const [index, setIndex] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const interval = setInterval(() => {
      setIndex((prev) => (prev + 1) % words.length);
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="landing-wrapper">
      <nav className="navbar">
        <div className="nav-left">
          <span className="nav-logo">
            <span className="gradient-text">HENA</span>
          </span>
        </div>
        <div className="nav-right">
          <button onClick={() => navigate('/login')}>Login</button>
        </div>
      </nav>

      <div className="landing-container">
        <img src="/logo.png" alt="Logo" className="landing-logo" />

        <div className="main-title">Event Management</div>

        <AnimatePresence mode="wait">
          <motion.div
            key={words[index]}
            className="changing-word"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.5 }}
          >
            {words[index]}
          </motion.div>
        </AnimatePresence>

        <p className="description">
          Deliver exceptional events with ease. Our powerful platform streamlines
          planning, maximizes engagement, and ensures unforgettable experiences every time.
        </p>

        <button className="get-started" onClick={() => navigate('/login')}>
          Get Started
        </button>

        <motion.div
          className="fade-all-icons"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 3 }}
        >
          {featureItems.map((item, index) => (
            <div className="feature-static" key={index}>
              {item.icon}
              <span>{item.label}</span>
            </div>
          ))}
        </motion.div>
      </div>
    </div>
  );
};

export default LandingPage;
