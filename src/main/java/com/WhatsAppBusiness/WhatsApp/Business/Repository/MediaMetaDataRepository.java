package com.WhatsAppBusiness.WhatsApp.Business.Repository;

import com.WhatsAppBusiness.WhatsApp.Business.Model.MediaMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaMetaDataRepository extends JpaRepository<MediaMetadata, Integer> {
}