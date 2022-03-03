package rnstudio.socreg

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build

class Notification(
    _context: Context,
    _channelID: String,
    _channelName: String,
    _description: String,
    _title: String,
    _importance: Int,
    _manager: NotificationManager?,
    _soundUriString: String?
) {
    private val context = _context
    private val channelID = _channelID
    private val channelName = _channelName
    private val description = _description
    private val title = _title
    private val importance = _importance
    private val manager = _manager
    private val soundUriString = _soundUriString

    fun createNotif(): Notification.Builder {
        val builder = Notification.Builder(context)

        val enableSound = false
        val enableVibration = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = importance
//            manager?.deleteNotificationChannel(channelID)
            var mChannel = manager!!.getNotificationChannel(channelID)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            if (mChannel == null) {
                mChannel = NotificationChannel(channelID, channelName, importance)
                mChannel.description = title
                mChannel.enableVibration(enableVibration)

                if(soundUriString != null) {
                    val soundUri: Uri =
                        Uri.parse(soundUriString)

                    if(enableSound) {
                        mChannel.setSound(soundUri, audioAttributes)
                    }
                }
                else{
                    if(enableSound) {
                        mChannel.setSound(
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                            audioAttributes
                        )
                    }
                }

                if(enableVibration) {
                    mChannel.vibrationPattern =
                        longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                }
                manager!!.createNotificationChannel(mChannel)
            }
            else{
                mChannel.enableVibration(enableVibration)
                if(soundUriString != null) {
                    val soundUri: Uri =
                        Uri.parse(soundUriString)

                    if(enableSound) {
                        mChannel.setSound(soundUri, audioAttributes)
                    }
                    else{
                        mChannel.setSound(null, null)
                    }
                }
                else{
                    if(enableSound) {
                        mChannel.setSound(
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                            audioAttributes
                        )
                    }
                    else{
                        mChannel.setSound(null, null)
                    }
                }
            }
            val builder2 = Notification.Builder(context, channelID)
            builder2.setContentTitle(title) // required
                .setSmallIcon(R.drawable.ic_profile) // required
                .setContentText(description) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
            return builder2
        } else {
            builder.setAutoCancel(true)
            builder.setContentTitle(title)
            builder.setContentText(description)
            builder.setSmallIcon(R.drawable.ic_profile)
            builder.setVisibility(Notification.VISIBILITY_PUBLIC)
            if(soundUriString != null) {
                val soundUri: Uri =
                    Uri.parse(soundUriString)

                if(enableSound) {
                    builder.setSound(soundUri)
                }
            }
            else {
                if(enableSound) {
                    builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                }
            }

            return builder
        }
    }
}