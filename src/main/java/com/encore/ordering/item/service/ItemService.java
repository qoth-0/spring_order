package com.encore.ordering.item.service;

import com.encore.ordering.item.domain.Item;
import com.encore.ordering.item.dto.ItemReqDto;
import com.encore.ordering.item.dto.ItemResDto;
import com.encore.ordering.item.dto.ItemSearchDto;
import com.encore.ordering.item.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional // 더티체킹을 해줌
public class ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item create(ItemReqDto itemReqDto) {
        MultipartFile multipartFile = itemReqDto.getItemImage();
        String fileName = multipartFile.getOriginalFilename(); // 확장자 포함 파일명
        Item newItem = Item.builder()
                .name(itemReqDto.getName())
                .category(itemReqDto.getCategory())
                .price(itemReqDto.getPrice())
                .stockQuantity(itemReqDto.getStockQuantity())
                .build();
        Item item = itemRepository.save(newItem);
        Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/", item.getId()+"_"+fileName); // 파일저장 경로
        item.setImagePath(path.toString()); // DB 반영
        try {
            byte[] bytes = multipartFile.getBytes(); // 이미지 파일 -> 바이트로 변환
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE); // 이미 있으면 덮어쓰기 없으면 create
        } catch (IOException e) {
            throw new IllegalArgumentException("image not available"); // 파일처리는 checkedException(예외 발생 시 롤백처리 x) -> 런타임에서 예외처리를 위해 Illegal로 바꿔줌
        }
//        더티체킹 되어 재저장 필요 x
        return item;
    }

    public Item delete(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not found Item"));
        item.deleteItem();
        return item;
    }

    public Resource getImage(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not fouund item"));
        String imagePath = item.getImagePath();
        Path path = Paths.get(imagePath);
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("url form is not valid");
        }
        return resource;
    }

    public Item update(Long id, ItemReqDto itemReqDto) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not fouund item"));
        MultipartFile multipartFile = itemReqDto.getItemImage();
        String fileName = multipartFile.getOriginalFilename(); // 확장자 포함 파일명
        Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/", item.getId()+"_"+fileName); // 파일저장 경로
        item.updateItem(itemReqDto.getName(), itemReqDto.getCategory(), itemReqDto.getPrice(), itemReqDto.getStockQuantity(), path.toString()); // DB 반영
        try {
            byte[] bytes = multipartFile.getBytes(); // 이미지 파일 -> 바이트로 변환
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE); // 이미 있으면 덮어쓰기 없으면 create
        } catch (IOException e) {
            throw new IllegalArgumentException("image not available"); // 파일처리는 checkedException(예외 발생 시 롤백처리 x) -> 런타임에서 예외처리를 위해 Illegal로 바꿔줌
        }
//        더티체킹 되어 저장 필요 x
        return item;
    }

    public List<ItemResDto> findAll(ItemSearchDto itemSearchDto, Pageable pageable) {
//        검색을 위해 specification 객체 사용
//        Specification객체는 복잡한 쿼리를 명세를 이용해 정의하여 쉽게 생성
        Specification<Item> spec = new Specification<Item>() { // 익명객체
            @Override
//            root : 엔티티의 속성을 접근하기 위한 객체, criteriaBuilder : 쿼리를 생성하기 위한 객체
            public Predicate toPredicate(Root<Item> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(itemSearchDto.getName() != null) {
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + itemSearchDto.getName() + "%"));
                }
                if(itemSearchDto.getCategory() != null) {
                    predicates.add(criteriaBuilder.like(root.get("category"), "%" + itemSearchDto.getCategory() + "%"));
                }
                predicates.add(criteriaBuilder.equal(root.get("delYn"),"N"));
                Predicate[] predicateArr = new Predicate[predicates.size()];
                for(int i=0; i< predicates.size(); i++) {
                    predicateArr[i] = predicates.get(i);
                }
                Predicate predicate = criteriaBuilder.and(predicateArr);
                return predicate;
            }
        };
        Page<Item> items = itemRepository.findAll(spec, pageable);
        List<Item> itemList = items.getContent();
        List<ItemResDto> itemResDtos = itemList.stream().map(i -> ItemResDto.builder()
                .name(i.getName())
                .price(i.getPrice())
                .category(i.getCategory())
                .stockQuantity(i.getStockQuantity())
                .imagePath(i.getImagePath())
                .id(i.getId())
                .build()).collect(Collectors.toList());
        return itemResDtos;
    }
}
