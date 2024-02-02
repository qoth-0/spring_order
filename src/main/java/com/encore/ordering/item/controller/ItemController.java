package com.encore.ordering.item.controller;

import com.encore.ordering.common.CommonResponse;
import com.encore.ordering.item.domain.Item;
import com.encore.ordering.item.dto.ItemReqDto;
import com.encore.ordering.item.dto.ItemResDto;
import com.encore.ordering.item.dto.ItemSearchDto;
import com.encore.ordering.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/item/create")
    public ResponseEntity<CommonResponse> itemCreate(ItemReqDto itemReqDto) {
        Item item = itemService.create(itemReqDto);
        return new ResponseEntity<>(
                new CommonResponse(HttpStatus.CREATED, "item successfully created", item.getId()), HttpStatus.CREATED);
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemResDto>> items(ItemSearchDto itemSearchDto, Pageable pageable) { // 검색 키워드에 따라 내용이 달라짐
        List<ItemResDto> itemResDtos = itemService.findAll(itemSearchDto, pageable);
        return new ResponseEntity<>(itemResDtos, HttpStatus.OK);
    }

    @GetMapping("/item/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable Long id) { // 검색 키워드에 따라 내용이 달라짐
        Resource resource = itemService.getImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(resource, headers, HttpStatus.OK); // CommonResponse로 씌우면 이미지가 잘 안나올 수 도 있으므로 사용 x
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/item/{id}/update")
    public ResponseEntity<CommonResponse> itemUpdate(@PathVariable Long id, ItemReqDto itemReqDto) {
        Item item = itemService.update(id, itemReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "item successfully updated", item.getId()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/item/{id}/delete")
    public ResponseEntity<CommonResponse> itemDelete(@PathVariable Long id) {
        Item item = itemService.delete(id);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "item successfully deleted", null), HttpStatus.OK);
    }
}
