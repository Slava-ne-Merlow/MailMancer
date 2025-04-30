import {useNavigate, useSearchParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import userStore from "../../../store/UserStore";
import styles from "./OrderDetailsPage.module.css";
import search from "../../../assets/icons/search.svg";

const OrderDetailsPage = () => {
    const [searchParams] = useSearchParams();
    const orderId = searchParams.get("orderId");
    const navigate = useNavigate();

    const [orderDetails, setOrderDetails] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!orderId) return;

        const fetchOrderDetails = async () => {
            setLoading(true);
            try {
                const response = await fetch(`http://localhost:8080/api/v1/order/${orderId}`, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `${userStore.token}`,
                    },
                });
                console.log("1231123");
                console.log(response);

                const data = await response.json();

                console.log(data);
                if (!response.ok) throw new Error(data.message || "Ошибка загрузки");

                setOrderDetails(data);
            } catch (err) {
                console.error(err);
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchOrderDetails();
    }, [orderId]);

    const handleBack = () => navigate("/mailings");
    function formatDate(inputDate) {
        const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

        const [year, month, day] = inputDate.split("-");
        return `${parseInt(day)} ${months[parseInt(month) - 1]} ${year}`;
    }

    return (
        <div className={styles.pageWrapper}>
            {loading && <div>Загрузка...</div>}
            {error && <div className={styles.error}>{error}</div>}
            {orderDetails && (
                <div className={styles.grid}>

                    <div className={styles.wrapper}>
                        <div className={styles.cardTitle}>
                            <div className={styles.leftSection}>
                                <button onClick={handleBack} className={styles.backButton}>Назад</button>
                                <div className={styles.title}>Заказ {orderDetails.number}</div>
                            </div>
                        </div>
                        <hr />
                        <div className={styles.cardContent}>
                            <h2>Info:</h2>
                            <div className={styles.tableWrapper}>
                                <table className={styles.table}>
                                    <thead>
                                    <tr>
                                        <th className={styles.col}>Created Date</th>
                                        <th className={styles.col}>From</th>
                                        <th className={styles.col}>To</th>
                                        <th className={styles.col}>Type</th>
                                        <th className={styles.col}>Status</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>{formatDate(orderDetails.created)}</td>
                                        <td>{orderDetails.from}</td>
                                        <td>{orderDetails.to}</td>
                                        <td>{orderDetails.kind}</td>
                                        <td><span className={styles.status}>Process</span></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>

                            <h2>Cargo Details:</h2>
                            <div className={styles.tableContainer}>
                                <div className={styles.tableHeader}>
                                    <table className={styles.table}>
                                        <thead>
                                        <tr>
                                            <th className={styles.col}>Quantity</th>
                                            <th className={styles.col}>Weight</th>
                                            <th className={styles.col}>Length</th>
                                            <th className={styles.col}>Width</th>
                                            <th className={styles.col}>Height</th>
                                        </tr>
                                        </thead>
                                    </table>
                                </div>
                                <div className={styles.tableBodyWrapper}>
                                    <table className={styles.table}>
                                        <tbody>
                                        {orderDetails.cargoDetails.map((space, index) => (
                                            <tr key={index}>
                                                <td className={styles.col}>X {space.quantity}</td>
                                                <td className={styles.col}>{space.weight} kg</td>
                                                <td className={styles.col}>{space.length} m</td>
                                                <td className={styles.col}>{space.width} m</td>
                                                <td className={styles.col}>{space.height} m</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className={styles.wrapper}>
                        <div className={styles.cardTitle}>
                            <input
                                className={styles.search}
                                placeholder="Search / Filter"
                                type="text"
                                style={{ backgroundImage: `url(${search})` }}
                            />
                        </div>
                        <hr />
                    </div>
                </div>
            )}
        </div>
    );
};

export default OrderDetailsPage;
