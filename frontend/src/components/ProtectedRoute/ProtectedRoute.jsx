import { Navigate } from "react-router-dom";
import userStore from "../../store/UserStore";

const ProtectedRoute = ({ children }) => {
    if (!userStore.isAuth) {
        return <Navigate to="/login" replace />;
    }
    return children;
};

export default ProtectedRoute;
