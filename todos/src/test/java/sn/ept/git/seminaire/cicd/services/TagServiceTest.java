package sn.ept.git.seminaire.cicd.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sn.ept.git.seminaire.cicd.entities.Tag;
import sn.ept.git.seminaire.cicd.exceptions.ItemExistsException;
import sn.ept.git.seminaire.cicd.exceptions.ItemNotFoundException;
import sn.ept.git.seminaire.cicd.mappers.TagMapper;
import sn.ept.git.seminaire.cicd.models.TagDTO;
import sn.ept.git.seminaire.cicd.repositories.TagRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository repository;

    @Mock
    private TagMapper mapper;

    @InjectMocks
    private TagService tagService;

    private TagDTO tagDTO;
    private Tag tagEntity;
    private String tagId;
    private String tagName;

    @BeforeEach
    void setUp() {
        tagId = UUID.randomUUID().toString();
        tagName = "Test Tag";

        tagDTO = new TagDTO();
        tagDTO.setId(tagId);
        tagDTO.setName(tagName);

        tagEntity = new Tag();
        tagEntity.setId(tagId);
        tagEntity.setName(tagName);
    }

    @Test
    void save_WhenTagDoesNotExist_ShouldReturnSavedTag() {

        when(repository.findByName(tagName)).thenReturn(Optional.empty());
        when(mapper.toEntity(any(TagDTO.class))).thenReturn(tagEntity);
        when(repository.saveAndFlush(tagEntity)).thenReturn(tagEntity);
        when(mapper.toDTO(tagEntity)).thenReturn(tagDTO);


        TagDTO result = tagService.save(tagDTO);


        assertNotNull(result);
        assertEquals(tagName, result.getName());
        verify(repository).findByName(tagName);
        verify(repository).saveAndFlush(tagEntity);
        verify(mapper).toEntity(any(TagDTO.class));
        verify(mapper).toDTO(tagEntity);
    }

    @Test
    void save_WhenTagAlreadyExists_ShouldThrowItemExistsException() {

        when(repository.findByName(tagName)).thenReturn(Optional.of(tagEntity));


        assertThrows(ItemExistsException.class, () -> tagService.save(tagDTO));
        verify(repository).findByName(tagName);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void delete_WhenTagExists_ShouldDeleteTag() {

        when(repository.findById(tagId)).thenReturn(Optional.of(tagEntity));


        tagService.delete(tagId);


        verify(repository).findById(tagId);
        verify(repository).deleteById(tagId);
    }

    @Test
    void delete_WhenTagNotFound_ShouldThrowItemNotFoundException() {

        when(repository.findById(tagId)).thenReturn(Optional.empty());


        assertThrows(ItemNotFoundException.class, () -> tagService.delete(tagId));
        verify(repository).findById(tagId);
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    void findById_WhenTagExists_ShouldReturnTag() {

        when(repository.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(mapper.toDTO(tagEntity)).thenReturn(tagDTO);


        TagDTO result = tagService.findById(tagId);


        assertNotNull(result);
        assertEquals(tagId, result.getId());
        assertEquals(tagName, result.getName());
        verify(repository).findById(tagId);
        verify(mapper).toDTO(tagEntity);
    }

    @Test
    void findById_WhenTagNotFound_ShouldThrowItemNotFoundException() {

        when(repository.findById(tagId)).thenReturn(Optional.empty());


        assertThrows(ItemNotFoundException.class, () -> tagService.findById(tagId));
        verify(repository).findById(tagId);
        verify(mapper, never()).toDTO(any());
    }

    @Test
    void findAll_WhenCalled_ShouldReturnPagedResults() {

        Pageable pageable = PageRequest.of(0, 10);
        List<Tag> tags = Arrays.asList(tagEntity);
        Page<Tag> tagPage = new PageImpl<>(tags, pageable, tags.size());

        when(repository.findAll(pageable)).thenReturn(tagPage);
        when(mapper.toDTO(tagEntity)).thenReturn(tagDTO);


        Page<TagDTO> result = tagService.findAll(pageable);


        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(tagDTO, result.getContent().get(0));
        verify(repository).findAll(pageable);
    }

    @Test
    void findAll_WhenNoTags_ShouldReturnEmptyPage() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(repository.findAll(pageable)).thenReturn(emptyPage);


        Page<TagDTO> result = tagService.findAll(pageable);


        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(repository).findAll(pageable);
    }

    @Test
    void update_WhenTagExistsAndNameIsUnique_ShouldUpdateTag() {

        String newName = "Updated Tag";
        TagDTO updateDTO = new TagDTO();
        updateDTO.setId(tagId);
        updateDTO.setName(newName);

        Tag updatedEntity = new Tag();
        updatedEntity.setId(tagId);
        updatedEntity.setName(newName);

        TagDTO updatedDTO = new TagDTO();
        updatedDTO.setId(tagId);
        updatedDTO.setName(newName);

        when(repository.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(repository.findByNameWithIdNotEquals(newName, tagId)).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(Tag.class))).thenReturn(updatedEntity);
        when(mapper.toDTO(updatedEntity)).thenReturn(updatedDTO);


        TagDTO result = tagService.update(tagId, updateDTO);


        assertNotNull(result);
        assertEquals(newName, result.getName());
        verify(repository).findById(tagId);
        verify(repository).findByNameWithIdNotEquals(newName, tagId);
        verify(repository).saveAndFlush(any(Tag.class));
        verify(mapper).toDTO(updatedEntity);
    }

    @Test
    void update_WhenTagNotFound_ShouldThrowItemNotFoundException() {

        when(repository.findById(tagId)).thenReturn(Optional.empty());


        assertThrows(ItemNotFoundException.class, () -> tagService.update(tagId, tagDTO));
        verify(repository).findById(tagId);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void update_WhenNameAlreadyExists_ShouldThrowItemExistsException() {

        String existingName = "Existing Tag";
        TagDTO updateDTO = new TagDTO();
        updateDTO.setId(tagId);
        updateDTO.setName(existingName);

        Tag existingTag = new Tag();
        existingTag.setId(UUID.randomUUID().toString());
        existingTag.setName(existingName);

        when(repository.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(repository.findByNameWithIdNotEquals(existingName, tagId)).thenReturn(Optional.of(existingTag));


        assertThrows(ItemExistsException.class, () -> tagService.update(tagId, updateDTO));
        verify(repository).findById(tagId);
        verify(repository).findByNameWithIdNotEquals(existingName, tagId);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void update_WhenSameNameForSameTag_ShouldUpdateSuccessfully() {

        when(repository.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(repository.findByNameWithIdNotEquals(tagName, tagId)).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(Tag.class))).thenReturn(tagEntity);
        when(mapper.toDTO(tagEntity)).thenReturn(tagDTO);


        TagDTO result = tagService.update(tagId, tagDTO);


        assertNotNull(result);
        assertEquals(tagName, result.getName());
        verify(repository).findById(tagId);
        verify(repository).findByNameWithIdNotEquals(tagName, tagId);
        verify(repository).saveAndFlush(any(Tag.class));
    }

    @Test
    void deleteAll_WhenCalled_ShouldDeleteAllTags() {

        tagService.deleteAll();


        verify(repository).deleteAll();
    }

    @Test
    void deleteAll_WhenRepositoryThrowsException_ShouldPropagateException() {

        doThrow(new RuntimeException("Database error")).when(repository).deleteAll();


        assertThrows(RuntimeException.class, () -> tagService.deleteAll());
        verify(repository).deleteAll();
    }
}