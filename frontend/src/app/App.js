import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import RegisterPage from "../pages/auth/register/RegisterPage";
import userStore from "../store/UserStore";
import LoginPage from "../pages/auth/login/LoginPage";
import MailingsPage from "../pages/main/mailings/MailingsPage";
import TeamPage from "../pages/main/teams/TeamPage";
import CarriersPage from "../pages/main/carriers/CarriersPage";
import CreatePage from "../pages/main/create/CreatePage";

function App() {
  return (
      <Router>
        <Routes>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />

            <Route path="/mailings" element={userStore.isAuth ? <MailingsPage /> : <Navigate to="/login" />} />
            <Route path="/teams"    element={userStore.isAuth ? <TeamPage />     : <Navigate to="/login" />} />
            <Route path="/carriers" element={userStore.isAuth ? <CarriersPage /> : <Navigate to="/login" />} />
            <Route path="/create"   element={userStore.isAuth ? <CreatePage />   : <Navigate to="/login" />} />

            <Route path="*" element={<Navigate to="/login" />} />

        </Routes>
      </Router>
  );
}

export default App;
