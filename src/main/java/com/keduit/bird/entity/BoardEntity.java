package com.keduit.bird.entity;

import com.keduit.bird.dto.BoardDTO;
import com.keduit.bird.dto.MemberFormDTO;
import com.keduit.bird.repository.MemberRepository;
import com.keduit.bird.service.MemberService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "admin_board")
public class BoardEntity extends BaseEntity{
    @Id//pk
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String adminBoardId;
    //이걸 작성자 = memberName으로 삼기.

    @Column
    private String boardPass;

    @Column(nullable = false)
    private String boardTitle;

    @Column(length = 500)
    private String boardContent;

    @Column
    private int boardHits;

    @Column
    private int fileAttached; // 1 or 0

    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BoardFileEntity> boardFileEntityList = new ArrayList<>();

    public static BoardEntity toSaveEntity(BoardDTO boardDTO, String memberName){
        BoardEntity boardEntity = new BoardEntity();
        //작성자를 사용자의 memberName으로.
        boardEntity.setAdminBoardId(memberName);
        boardEntity.setBoardPass(boardDTO.getBoardPass());
        boardEntity.setBoardContent(boardDTO.getBoardContent());
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardHits(0);
        boardEntity.setFileAttached(0); // 파일 없음

        return  boardEntity;
    }
    public static BoardEntity toUpdateEntity(BoardDTO boardDTO, String memberName) {
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(boardDTO.getId());
        //작성자를 사용자의 memberName으로.
        boardEntity.setAdminBoardId(memberName);
        boardEntity.setBoardPass(boardDTO.getBoardPass());
        boardEntity.setBoardContent(boardDTO.getBoardContent());
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardHits(boardDTO.getBoardHits());
        return boardEntity;
    }

    public static BoardEntity toSaveFileEntity(BoardDTO boardDTO, String memberName) {
        BoardEntity boardEntity = new BoardEntity();
        //작성자를 사용자의 memberName으로.
        boardEntity.setAdminBoardId(memberName);
        boardEntity.setBoardPass(boardDTO.getBoardPass());
        boardEntity.setBoardContent(boardDTO.getBoardContent());
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardHits(0);
        boardEntity.setFileAttached(1); //파일있음.

        return  boardEntity;
    }

}
