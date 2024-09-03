package com.nexters.bottles.app.user.service

import com.nexters.bottles.app.user.domain.BlockContact
import com.nexters.bottles.app.user.repository.BlockContactRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BlockContactListService(
    private val blockContactListRepository: BlockContactRepository,
) {

    @Transactional(readOnly = true)
    fun findAllByUserId(userId: Long): List<BlockContact> {
        return blockContactListRepository.findAllByUserId(userId)
    }

    @Transactional
    fun saveAll(newBlockContactList: List<BlockContact>) {
        blockContactListRepository.saveAll(newBlockContactList)
    }

    @Transactional
    fun deleteAll(newBlockContacts: List<BlockContact>) {
        blockContactListRepository.deleteAll(newBlockContacts)
    }

    @Transactional(readOnly = true)
    fun findAllByPhoneNumber(phoneNumber: String): List<BlockContact> {
        return blockContactListRepository.findAllByPhoneNumber(phoneNumber)
    }
}
