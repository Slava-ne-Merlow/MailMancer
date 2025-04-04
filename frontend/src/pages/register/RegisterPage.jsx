import style from "./RegisterFrom.module.css";
import {useSearchParams} from "react-router-dom";
import RegisterManagerForm from "../../components/forms/RegisterManagerForm";
import RegisterHeadForm from "../../components/forms/RegisterHeadForm";

const RegisterPage = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");

    return (
        <>
            {token ? (
                <RegisterManagerForm token={token} style={style}/>
            ) : (
                <RegisterHeadForm style={style}/>
            )}
        </>
    );
};

export default RegisterPage;
