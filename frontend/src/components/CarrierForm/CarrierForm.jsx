import React, {useState} from 'react';
import styles from './CarrierForm.module.css';
import FileUpload from '../CustomFileUpload/FileUpload';
import plusIcon from '../../assets/icons/plus.svg';
import minusIcon from '../../assets/icons/minnus.svg';
import userStore from "../../store/UserStore";


const CarrierForm = ({ onSuccess }) => {
    const [errors, setErrors] = useState({
        companyName: false,
        emails: [],
        names: []
    });

    const handleFieldFocus = (index, field) => {
        if (field === 'email') {
            const updatedErrors = { ...errors };

            updatedErrors.emails[index] = false;
            setErrors(updatedErrors);

        } else if (field === 'name') {
            const updatedErrors = { ...errors };

            updatedErrors.names[index] = false;
            setErrors(updatedErrors);

        } else if (field === 'companyName') {
            setErrors({ ...errors , [field]: false });
        }
    };

    const [contacts, setContacts] = useState([
        { name: '', email: '', phoneNumber: '' }
    ]);

    const [formData, setFormData] = useState({
        companyName: '',
        comment: '',
        application: null,
        contract: null,
    });

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleFileChange = (name, file) => {
        setFormData({ ...formData, [name]: file });
    };

    const handleContactChange = (index, field, value) => {
        const updatedContacts = [...contacts];
        updatedContacts[index][field] = value;
        setContacts(updatedContacts);
    };

    const addContact = () => {
        setContacts([...contacts, { name: '', email: '', phoneNumber: '' }]);
    };

    const takeOfContact = () => {
        const newContacts = [...contacts];
        newContacts.pop()
        setContacts(newContacts);
    };

    const validateForm = () => {
        const newErrors = {
            companyName: !formData.companyName.trim(),
            emails: contacts.map((contact) => !isValidEmail(contact.email)),
            names: contacts.map((contact) => !contact.name.trim()),
        };


        setErrors(newErrors);

        return !newErrors.companyName && newErrors.emails.every((e) => !e);
    };
    const isValidEmail = (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };


    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) return;


        const data = new FormData();
        data.append("companyName", formData.companyName.trim());
        data.append("comment", formData.comment.trim());
        data.append("contacts", JSON.stringify(contacts));
        if (formData.application) data.append("application", formData.application);
        if (formData.contract) data.append("contract", formData.contract);
        console.log('Submitting:', data);
        console.log(JSON.stringify(contacts));
        try {
            const response = await fetch('http://localhost:8080/api/v1/carriers/create', {
                method: 'POST',
                body: data,
                headers: {
                    "Authorization": `${userStore.token}`,
                },
            });

            if (response.ok) {
                if (onSuccess) onSuccess();

            } else {
                alert('Ошибка при отправке формы');
            }
        } catch (error) {
            console.error('Ошибка при отправке:', error);
            alert('Ошибка соединения с сервером');
        }

    };


    return (
        <form className={styles.inlineForm} onSubmit={handleSubmit}>
            <div className={styles.formHeader}>New Carrier Company</div>
            <div className={styles.input}>
                <div className={styles.inputLabel}>Company Name</div>
                <input
                    className={`${styles.inputField} ${errors.companyName ? styles.inputFieldError : ''}`}
                    name="companyName"
                    value={formData.companyName}
                    onChange={handleInputChange}
                    onFocus={() => handleFieldFocus(0, 'companyName')}
                />
                {errors.companyName && (
                    <div className={styles.errorMessage}>Field is required</div>
                )}
            </div>

            <div className={styles.input}>
                <div className={styles.inputLabel}>Comment</div>
                <textarea
                    className={styles.inputArea}
                    name="comment"
                    value={formData.comment}
                    onChange={handleInputChange}
                />
            </div>

            <div className={styles.files}>
                <FileUpload
                    label="Application form"
                    name="application"
                    onFileChange={(file) => handleFileChange('application', file)}
                />
                <FileUpload
                    label="Contract"
                    name="contract"
                    onFileChange={(file) => handleFileChange('contract', file)}
                />
            </div>

            <div className={styles.formHeader}>Contacts</div>
            <div className={styles.contacts}>
                {['name', 'email', 'phoneNumber'].map((field) => (
                    <div className={styles.input} key={field}>
                        <div className={styles.inputLabel}>
                            {field.charAt(0).toUpperCase() + field.slice(1)}
                        </div>
                        {contacts.map((contact, rowIdx) => (
                            <div key={`${field}-${rowIdx}`} style={{ position: 'relative' }}>
                                <input
                                    className={`${styles.inputField} ${
                                        (field === 'email' && errors.emails?.[rowIdx]) || (field === 'name' && errors.names?.[rowIdx])  ? styles.inputFieldError : ''
                                    }`}
                                    type={field === 'email' ? 'email' : 'text'}
                                    value={contact?.[field] || ''}
                                    onChange={(e) =>
                                        handleContactChange(rowIdx, field, e.target.value)
                                    }
                                    onFocus={() => handleFieldFocus(rowIdx, field)}
                                />
                            </div>
                        ))}
                    </div>
                ))}

            </div>

            <div className={styles.buttonWrapper}>
                {contacts.length > 1 && (
                    <div className={styles.plusButton} onClick={takeOfContact}>
                        <img className={styles.plus} src={minusIcon} alt="add"/>
                    </div>
                )}
                <div className={styles.plusButton} onClick={addContact}>
                    <img className={styles.plus} src={plusIcon} alt="add"/>
                </div>

            </div>


            <button type="submit" className={styles.submitButton}>
                <div className={styles.text}>Add</div>
            </button>
        </form>
    );
};

export default CarrierForm;
