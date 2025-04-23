import styles from "./OrderCard.module.css";
import search from "../../assets/icons/search.svg";
import lock from "../../assets/icons/lock.svg";
import React, {useEffect, useState} from "react";
import userStore from "../../store/UserStore";
import {useCallback} from "react";


const OrderCard = ({closed}) => {
    const [orders, setOrders] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const [filteredOrders, setFilteredOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    const getData = useCallback(async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/v1/orders/${closed}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `${userStore.token}`,
                },
            });

            const data = await response.json();

            if (!response.ok) {
                alert("Ошибка запроса: " + (data.message || response.status));
                return [];
            }

            return data;
        } catch (error) {
            console.error("Ошибка сети:", error);
            alert("Ошибка сети. Попробуйте еще раз.");
            return [];
        }
    }, [closed]);

    useEffect(() => {
        const fetchOrders = async () => {
            setLoading(true);
            const result = await getData();
            setOrders(result);
            setFilteredOrders(result);
            setLoading(false);
        };


        fetchOrders();
    }, [closed, getData]);

    useEffect(() => {
        const searchWords = searchQuery.toLowerCase().split(" ").filter(Boolean);
        const filtered = orders.filter(order =>
            searchWords.every(word =>
                (order.trackNumber?.toLowerCase().includes(word)) ||
                (order.from?.toLowerCase().includes(word)) ||
                (order.to?.toLowerCase().includes(word)) ||
                (order.type?.toLowerCase().includes(word))
            )
        );

        setFilteredOrders(filtered);
    }, [searchQuery, orders]);

    return (
        <div className={styles.card}>
            <div className={styles.cardTitle}>
                <div className={styles.leftSection}>
                    <div className={styles.title}>
                        {closed ? "Closed" : "Open"}
                    </div>
                    {closed && <img className={styles.lock} alt="lock" src={lock}/>}
                </div>

                <input
                    className={styles.search}
                    placeholder="Search / Filter"
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    style={{backgroundImage: `url(${search})`}}
                />
            </div>

            <div className={styles.mailingGroup}>
                {loading ? (
                    <>
                        <hr/>
                        <div className={styles.mailing}>
                            <div>Loading...</div>
                        </div>
                    </>
                ) : orders.length > 0 ? (
                    filteredOrders.length > 0 ? (
                        filteredOrders.map((order) => (
                            <React.Fragment key={order.id}>
                                <hr/>
                                <div className={styles.mailing}>
                                    <div className={styles.tracNumber}>{order.trackNumber}</div>
                                    <div className={styles.route}>
                                        <span className={styles.city}>{order.from}</span>
                                        <span className={styles.dash}>-</span>
                                        <span className={styles.city}>{order.to}</span>
                                    </div>
                                    <div className={styles.tracNumber}>{order.type}</div>
                                </div>
                            </React.Fragment>

                        ))) : (
                        <>
                            <hr/>
                            <div className={styles.mailing}>
                                <div>Рассылок не найдено</div>
                            </div>
                        </>
                    )
                ) : (
                    <>
                        <hr/>
                        <div className={styles.mailing}>
                            <div>Нет рассылок</div>
                        </div>
                    </>
                )}


            </div>
        </div>
    );
};

export default OrderCard;


