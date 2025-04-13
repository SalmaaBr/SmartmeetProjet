package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.GhanemRidene.IFoundItemServices;
import tn.esprit.examen.Smartmeet.entities.GhanemRidene.FoundItem;


import java.util.List;

@RequiredArgsConstructor
@RequestMapping("FoundObject")
@RestController
@Tag(name="hello")
public class FoundItemRestController {



    private final IFoundItemServices foundItemService;

    @PostMapping
    public ResponseEntity<FoundItem> createFoundItem(@RequestBody FoundItem foundItem) {
        FoundItem createdFoundItem = foundItemService.createFoundItem(foundItem);
        return new ResponseEntity<>(createdFoundItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoundItem> updateFoundItem(@PathVariable Long id, @RequestBody FoundItem foundItem) {
        FoundItem updatedFoundItem = foundItemService.updateFoundItem(id, foundItem);
        return new ResponseEntity<>(updatedFoundItem, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoundItem(@PathVariable Long id) {
        foundItemService.deleteFoundItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoundItem> getFoundItemById(@PathVariable Long id) {
        FoundItem foundItem = foundItemService.getFoundItemById(id);
        return foundItem != null ? new ResponseEntity<>(foundItem, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<FoundItem>> getAllFoundItems() {
        List<FoundItem> foundItems = foundItemService.getAllFoundItems();
        return new ResponseEntity<>(foundItems, HttpStatus.OK);
    }
  @PostMapping("/{foundItemId}/assignEvent/{eventId}")
  public ResponseEntity<FoundItem> assignFoundItemToEvent(@PathVariable Long foundItemId, @PathVariable Long eventId) {
    FoundItem foundItem = foundItemService.assignFoundItemToEvent(foundItemId, eventId);
    if (foundItem != null) {
      return ResponseEntity.ok(foundItem);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @PostMapping("/{foundItemId}/assignUser/{userId}")
  public ResponseEntity<FoundItem> assignFoundItemToUser(@PathVariable Long foundItemId, @PathVariable Long userId) {
    FoundItem foundItem = foundItemService.assignFoundItemToUser(foundItemId, userId);
    if (foundItem != null) {
      return ResponseEntity.ok(foundItem);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }
}
