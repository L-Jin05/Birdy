package com.keduit.bird.dto;

import com.keduit.bird.entity.BoardEntity;
import com.keduit.bird.entity.BoardFileEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Long id;
    private String adminBoardId;
    private String boardTitle;
    private String boardContent;
    private String boardPass;
    private int boardHits;//조회수
    private LocalDateTime boardCreatedTime;//게시글 생성일자
    private LocalDateTime boardUpDatedTime;//게시글 수정일자

    private List<MultipartFile> boardFile; //save.html->Controller 파일담는용도
    private List<String> originalFileName; //원본 파일이름
    private List<String> storedFileName;//서버 저장용 파일이름
    private int fileAttached; //파일 첨부 여부(첨부 1, 미첨부 0)


    public BoardDTO(Long id, String adminBoardId, String boardTitle,
                    int boardHits, LocalDateTime boardCreatedTime) {
        this.id = id;
        this.adminBoardId = adminBoardId;
        this.boardTitle = boardTitle;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }

    public static BoardDTO toBoardDTO(BoardEntity boardEntity){
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setAdminBoardId(boardEntity.getAdminBoardId());
        boardDTO.setBoardContent(boardEntity.getBoardContent());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardPass(boardEntity.getBoardPass());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getBoardCreatedTime());
        boardDTO.setBoardUpDatedTime(boardEntity.getBoardUpDatedTime());
        if (boardEntity.getFileAttached() == 0){
            boardDTO.setFileAttached(boardEntity.getFileAttached());//0
        }else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            boardDTO.setFileAttached((boardEntity.getFileAttached()));//1
            for(BoardFileEntity boardFileEntity: boardEntity.getBoardFileEntityList()) {
                originalFileNameList.add(boardFileEntity.getOriginalFileName());
                storedFileNameList.add(boardFileEntity.getOriginalFileName());
            }
            boardDTO.setOriginalFileName(originalFileNameList);
            boardDTO.setStoredFileName(storedFileNameList);
//            //파일 이름 가져가기_ 단일 파일 코드
//            // orginalFileName, storedFileName : board_file_table(BoardFileEntity)
//            // join
//            // select * from board_table b, board_file_table bf where b.id=bf.board_id
//            // and where b.id=?
//            boardDTO.setOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
//            boardDTO.setStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());
            }

        return boardDTO;

    }
}
