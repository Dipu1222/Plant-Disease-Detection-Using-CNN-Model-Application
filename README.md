# 🌿 Plant Disease Detection using CNN + Android App

This project is an **end-to-end plant disease detection system**.  
It includes:  
1. **Machine Learning model training (TensorFlow/Keras)** using a combined plant leaf disease dataset.  
2. **Trained TensorFlow Lite model** for deployment on Android.  
3. **Android app** that allows users to capture or upload an image of a leaf and get predictions about the disease with **cause and treatment info in English & Bangla**.

---

## 📂 Dataset

We used the **Leaf Disease Dataset Combination** from Kaggle:  
🔗 [Leaf Disease Dataset (Kaggle)](https://drive.google.com/file/d/1_54rxmRQi9pHFhF-OBkQH7qR8lCWziRe/view?usp=sharing)  

- Total Images: **39400**
- Classes: **17 plant disease + healthy classes**
- Train / Validation / Test split already available in the dataset

---

## 🧠 Model Training

The model is a **CNN (Convolutional Neural Network)** with **Batch Normalization, Dropout, and Data Augmentation** for better generalization.  
Trained on **Google Colab** using a **T4 GPU**.  

### Model Architecture:
- **4 Convolutional Blocks**
- **GlobalAveragePooling**
- **Dense layers with Dropout**
- **Output Layer: 17 softmax classes**

### Key Parameters:
- Image Size: **128x128**
- Batch Size: **32**
- Optimizer: **Adam**
- Loss: **Categorical Crossentropy**
- Epochs: **20 (EarlyStopping applied)**
---

## 📊 Model Performance

- Training Accuracy: ~95.33%
- Validation Accuracy: ~95.87%
- The model performs well on unseen data with generalizable predictions.

---

## 📱 Android App

We converted the trained model into **TensorFlow Lite (.tflite)** and integrated it into an Android app.

### App Features:
✅ Capture a photo using the camera or select from the gallery  
✅ Predicts disease and shows result instantly  
✅ Tap prediction to view **cause & treatment (English + Bangla)**  
✅ Handles errors (like no image selected)  
✅ Simple and professional **Material Design UI**

### Supported Classes:
- **Apple:** Apple Scab, Black Rot, Cedar Apple Rust, Healthy  
- **Cherry:** Powdery Mildew, Healthy  
- **Corn:** Cercospora Leaf Spot, Common Rust, Northern Leaf Blight, Healthy  
- **Grape:** Black Rot, Esca (Black Measles), Leaf Blight, Healthy  
- **Potato:** Early Blight, Late Blight, Healthy  


---

## 🚀 How to Run

### 🔹 Train the Model
1. Clone the repo:
   ```bash
   git clone https://github.com/yourusername/Plant-Disease-Detection.git
   cd Plant-Disease-Detection
