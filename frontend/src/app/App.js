import React from "react";
import {BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom";
import RegisterPage from "../pages/auth/register/RegisterPage";
import LoginPage from "../pages/auth/login/LoginPage";
import MailingsPage from "../pages/main/mailings/MailingsPage";
import TeamPage from "../pages/main/team/TeamPage";
import CarriersPage from "../pages/main/carriers/CarriersPage";
import CreatePage from "../pages/main/create/CreatePage";
import ProtectedRoute from "../components/ProtectedRoute/ProtectedRoute";

function App() {
    return (
        <Router>
            <Routes>

                <Route path="/register" element={<RegisterPage/>}/>
                <Route path="/login" element={<LoginPage/>}/>

                <Route path="/mailings" element={<ProtectedRoute><MailingsPage/></ProtectedRoute>}/>
                <Route path="/team" element={<ProtectedRoute><TeamPage/></ProtectedRoute>}/>
                <Route path="/carriers" element={<ProtectedRoute><CarriersPage/></ProtectedRoute>}/>
                <Route path="/create" element={<ProtectedRoute><CreatePage/></ProtectedRoute>}/>

                <Route path="*" element={<Navigate to="/login"/>}/>

            </Routes>
        </Router>
    );
}

export default App;
