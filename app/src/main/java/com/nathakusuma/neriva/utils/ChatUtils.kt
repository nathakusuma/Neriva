package com.nathakusuma.neriva.utils

import com.nathakusuma.neriva.data.model.SenderType

/**
 * Utility functions for chat-related operations
 */
object ChatUtils {

    /**
     * Get author name from sender type
     *
     * @param senderType The sender type (USER or PET)
     * @param petName The pet's name
     * @return Author display name
     */
    fun getAuthorName(
        senderType: SenderType,
        petName: String?,
    ): String {
        return when (senderType) {
            SenderType.USER -> "You"
            SenderType.PET -> petName ?: "Pet"
        }
    }
}

