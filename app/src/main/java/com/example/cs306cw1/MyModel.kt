package com.example.cs306cw1

/**
 * Represents the article model
 */
class MyModel {
    private var modelImage: String = ""
    var modelName: String? = null
    var modelURL: String = ""


    /**
     * Gets the image url
     * @return image url as string
     */
    fun getImages(): String {
        return modelImage
    }

    /**
     * Sets the image url
     * @param image_drawable image url as string
     */
    fun setImages(image_drawable: String){
        this.modelImage = image_drawable
    }

    /**
     * get the article name
     * @return the name of the article
     */
    fun getNames(): String {
        return modelName.toString()
    }

    /**
     * Set the article name
     * @param name the name of the article
     */
    fun setNames(name: String){
        this.modelName = name
    }

    /**
     * Set the article url
     * @param url the article url
     */
    fun setURL(url: String){
        this.modelURL = url
    }

    /**
     * Get the article url
     * @return The article url
     */
    fun getURL(): String{
        return modelURL
    }
}