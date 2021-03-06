package com.nursetracking.nursetrackingdashboardmqtt.services.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MQTTHelper(private val mContext: Context) {

    lateinit var mqttAndroidClient: MqttAndroidClient
    val serverUriFWG = "tcp://10.32.11.18:1883"
    val serverUriHome = "tcp://192.168.1.49:1883"
    val serverUriLong = "tcp://192.168.43.129:1883"
    val serverLocal = "tcp://192.168.43.234:1883"
    val mqttServerHTTP = "tcp://test.mosquitto.org"

    val serverUri = mqttServerHTTP
    private val clientId = "Dashboard"
    var subscriptionTopic = "Phyathai/Ward1/Dashboard"
    private val username = ""
    private val password = ""

    fun init() {
        mqttAndroidClient = MqttAndroidClient(mContext, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("MQTT connectComplete", s)
            }

            override fun connectionLost(throwable: Throwable) {
                Log.d("MQTT connectionLost","Disconnect")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.d("Mqtt messageArrived", mqttMessage.toString())
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {

            }
        })
        connect()
    }

    fun setCallback(callback: MqttCallbackExtended) {
        mqttAndroidClient.setCallback(callback)
    }

    private fun connect() {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        //mqttConnectOptions.userName = username
        //mqttConnectOptions.password = password.toCharArray()

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                    subscribeToTopic()
                    Log.w("Mqtt", "Success to connect to: " + serverUri )
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString())
                }
            })

        } catch (ex: MqttException) {
            ex.printStackTrace()
        }

    }

    private fun subscribeToTopic() {
        val subscriptionTopic = "Phyathai/Ward1/Dashboard"
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.w("Mqtt", subscriptionTopic+ " Subscribed!")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.w("Mqtt", "Subscribed fail!")
                }
            })

        } catch (ex: MqttException) {
            System.err.println("Exceptionst subscribing")
            ex.printStackTrace()
        }
    }

}