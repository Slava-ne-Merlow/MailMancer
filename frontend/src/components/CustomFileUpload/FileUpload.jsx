import React, { useRef, useState } from 'react';
import styles from './FileUpload.module.css'; // Убедитесь, что пути корректны
import fileIcon from "../../assets/icons/file.svg";
const FileUpload = ({ label, name, onFileChange }) => {
    const fileInputRef = useRef(null);
    const [selectedFile, setSelectedFile] = useState(null);

    const handleClick = () => {
        if (selectedFile) {
            setSelectedFile(null);
            fileInputRef.current.value = null;
            onFileChange && onFileChange(null); // сообщаем родителю
        } else {
            fileInputRef.current.click();
        }
    };

    const handleChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            setSelectedFile(file);
            onFileChange && onFileChange(file); // отправляем файл наверх
        }
    };

    return (
        <div className={styles.input}>
            <div className={styles.inputLabel}>{label}</div>

            <div
                className={`${styles.inputFile} ${selectedFile ? styles.selected : ''}`}
                onClick={handleClick}
                title={selectedFile ? selectedFile.name : undefined}
            >
                <div className={styles.text}>
                    {selectedFile ? selectedFile.name : 'Выбрать файл'}
                </div>
                <img src={fileIcon} alt="file" className={styles.icon} />
            </div>

            <input
                type="file"
                ref={fileInputRef}
                name={name}
                style={{ display: 'none' }}
                onChange={handleChange}
            />
        </div>
    );
};

export default FileUpload;