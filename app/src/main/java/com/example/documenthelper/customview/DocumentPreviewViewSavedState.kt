package com.example.documenthelper.customview

import android.os.Build
 import android.os.Parcel
 import android.os.Parcelable
 import android.view.View
 
 class DocumentPreviewViewSavedState : View.BaseSavedState { //Реализует Parcelable
 
     private lateinit var state: DocumentPreviewUiState

     constructor(superState: Parcelable?) : super(superState)
 
     private constructor(parcelIn: Parcel) : super(parcelIn) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
             state = parcelIn.readSerializable(
                 DocumentPreviewUiState::class.java.classLoader,
                 DocumentPreviewUiState::class.java
             ) as DocumentPreviewUiState
         } else {
             parcelIn.readSerializable() as DocumentPreviewUiState
         }
     }
 
     override fun writeToParcel(out: Parcel, flags: Int) { //Вызывается при уничтожении View
         super.writeToParcel(out, flags)
         out.writeSerializable(state)
     }
 
     fun restore(): DocumentPreviewUiState = state
 
     fun save(uiState: DocumentPreviewUiState) {
         state = uiState
     }
 
     override fun describeContents(): Int = 0
 
     companion object CREATOR :
         Parcelable.Creator<DocumentPreviewViewSavedState> { //Отвечает за создание объекта Parcel
 
         override fun createFromParcel(parcel: Parcel): DocumentPreviewViewSavedState =
             DocumentPreviewViewSavedState(parcel)
 
 
         override fun newArray(size: Int): Array<DocumentPreviewViewSavedState?> =
             arrayOfNulls(size)
 
     }
 }