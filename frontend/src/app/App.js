import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import HomePage from "../pages/home/HomePage";
import RegisterPage from "../pages/register/RegisterPage";
import userStore from "../store/UserStore";
import LoginPage from "../pages/login/LoginPage";
import MailingsPage from "../pages/mailings/MailingsPage";
import TeamPage from "../pages/teams/TeamPage";
import CarriersPage from "../pages/carriers/CarriersPage";
import CreatePage from "../pages/create/CreatePage";

function App() {
  return (
      <Router>
        <Routes>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route
              path="/home"
              element={userStore.isAuth ? <HomePage /> : <Navigate to="/register" />}
          />
            <Route path="/mailings" element={<MailingsPage />} />
            <Route path="/teams"    element={<TeamPage />} />
            <Route path="/carriers" element={<CarriersPage />} />
            <Route path="/create" element={<CreatePage />} />

            <Route path="*" element={<Navigate to="/login" />} />

        </Routes>
      </Router>
  );
}

export default App;
