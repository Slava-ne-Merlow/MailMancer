import {useSearchParams} from "react-router-dom";
import RegisterManagerForm from "../../../components/RegisterManagerForm/RegisterManagerForm";
import RegisterHeadForm from "../../../components/RegisterHeadForm/RegisterHeadForm";

const RegisterPage = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");

    return (
        <>
            {token ? (
                <RegisterManagerForm token={token}/>
            ) : (
                <RegisterHeadForm/>
            )}
        </>
    );
};

export default RegisterPage;
