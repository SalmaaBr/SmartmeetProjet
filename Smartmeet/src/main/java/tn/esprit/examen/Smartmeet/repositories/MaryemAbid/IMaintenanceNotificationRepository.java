package tn.esprit.examen.Smartmeet.repositories.MaryemAbid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.Smartmeet.entities.MaryemAbid.MaintenanceNotification;

import java.util.List;

@Repository
public interface IMaintenanceNotificationRepository extends JpaRepository<MaintenanceNotification, Long> {
    
    /**
     * Find all notifications for a specific user, ordered by creation date (newest first)
     */
    List<MaintenanceNotification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Count notifications with a specific status for a user
     */
    int countByUserIdAndStatus(Long userId, String status);
    
    /**
     * Check if a notification already exists for a user, reservation, and maintenance start date
     */
    boolean existsByUserIdAndReservationIdAndMaintenanceStartDate(Long userId, Integer reservationId, String maintenanceStartDate);
    
    /**
     * Find notifications for a specific resource
     */
    List<MaintenanceNotification> findByResourceId(Integer resourceId);
    
    /**
     * Find notifications related to a specific reservation
     */
    List<MaintenanceNotification> findByReservationId(Integer reservationId);
    
    /**
     * Find all notifications for a user by username, ordered by creation date (newest first)
     */
    @Query("SELECT n FROM MaintenanceNotification n JOIN Users u ON n.userId = u.userID WHERE u.username = :username ORDER BY n.createdAt DESC")
    List<MaintenanceNotification> findByUsernameOrderByCreatedAtDesc(@Param("username") String username);
    
    /**
     * Count notifications with a specific status for a user by username
     */
    @Query("SELECT COUNT(n) FROM MaintenanceNotification n JOIN Users u ON n.userId = u.userID WHERE u.username = :username AND n.status = :status")
    int countByUsernameAndStatus(@Param("username") String username, @Param("status") String status);
} 